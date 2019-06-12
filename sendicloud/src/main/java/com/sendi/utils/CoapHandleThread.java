package com.sendi.utils;

import com.sendi.entity.Device;
import com.sendi.entity.Product;
import com.sendi.entity.Raspberry;
import com.sendi.entity.RaspberryUserMerge;
import com.sendi.service.impl.DeviceService;
import com.sendi.service.impl.ProductServiceImpl;
import com.sendi.service.impl.RaspberryService;
import com.sendi.service.impl.RaspberryUserMergeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * 设备注册处理线程
 * Created by fengzm on 2019/2/2.
 */
public class CoapHandleThread implements Runnable {
    private Logger logger = LoggerFactory.getLogger(CoapHandleThread.class);
    private DatagramSocket socket;
    private DatagramPacket packet;
    private int udpPort;
    private String content;
    private DeviceService deviceService;
    private ProductServiceImpl productService;
    private RaspberryService raspberryService;
    private RaspberryUserMergeService raspberryUserMergeService;
    private InetAddress address;
    private String addr;
    public CoapHandleThread(DatagramSocket socket, DatagramPacket packet, ProductServiceImpl productService, RaspberryService raspberryService, RaspberryUserMergeService raspberryUserMergeService, DeviceService deviceService) {
        this.socket = socket;
        this.packet = packet;
        this.productService = productService;
        this.raspberryService = raspberryService;
        this.raspberryUserMergeService = raspberryUserMergeService;
        this.deviceService = deviceService;
    }

    @Override
    public void run() {
        address = packet.getAddress(); //实际环境中使用
        udpPort = packet.getPort();  //实际环境中使用
        addr = address.toString().replace("/", "");
        content = new String(packet.getData(), 0, packet.getLength()); //拼接字符串, 收到的数据
        logger.info("注册服务器接收到信息:" + content);
        System.out.println("注册服务器接收到信息:" + content);
        if (StringUtils.isEmpty(content)) {
            logger.info("数据为空，不做处理");
            return;
        }
        String[] info = content.split(":");//截取收到的数据 data:
        if (info.length == 0) {
            logger.info("数据格式错误，不做处理");
            return;
        }
        //设备注册消息，进行discover,observer
        if ("register".equals(info[0])) {
            this.register();
        } else if ("deregister".equals(info[0])){
            this.deregister();
        } else {
            logger.info("data receive: data empty error");
        }

    }
    /**
     * 注册
     */
    private void register() {
        //截取字符串得出 IMEI、树莓派ID，查询dev_config 是否是本公司的设备
        String context = content.replace("register:", "");
        String[] data1 = context.split(",");
        Map<String, String> map = new HashMap<String, String>();
        for (int i = 0; i < data1.length; i++) {
            String data2[] = data1[i].split(":");
            map.put(data2[0], data2[1]);
        }
        String hashResult = map.get("hash_result");//从注册信息中截取出来
        String exp = map.get("exp_id");
        Integer proId = Integer.parseInt(exp);
        Raspberry raspberry = raspberryService.queryInfoByHashResult(hashResult);
        String snCode = raspberryService.querySNByHashResult(hashResult);
        logger.info(" Raspberry信息" + raspberry);
        if (raspberryService.queryByHashResult(hashResult) > 0) {
            logger.info("是本公司树莓派设备：" + raspberry );
        } else {
            //如果不是本公司的设备。发送ERROR, 断开
            sendError();
            logger.info("不是本公司的树莓派，关闭连接:"+ raspberry);
            return;
        }
        //判断产品是否已经创建
        int n = productService.productList(proId);
        if(n<=0){
            sendError();
            logger.info("产品未创建，请先创建产品！");
            return;
        }
        //根据pro_id查出user_id
        Product product = productService.queryByProId(proId);
        String userId = product.getUserId();
        String protocol = product.getProtocol();
        Integer commitStatus = product.getSubmitStatus();
        //判断协议是否是COAP
        if(!("COAP".equals(protocol))){
            sendError();
            logger.info("协议不匹配");
            return;
        }
        if(commitStatus >= 1){
            sendError();
            logger.info("COAP-该产品已经提交过了");
            return;
        }
        //绑定树莓派与用户的关系;添加到关系表raspberry_user_merge
        int num = raspberryUserMergeService.queryNumBySnCode(snCode);
        RaspberryUserMerge raspberryUserMerge = new RaspberryUserMerge();
        if(num <= 0){
            raspberryUserMerge.setSnCode(snCode);
            raspberryUserMerge.setUserId(userId);
            raspberryUserMergeService.addRaspberryUserMerge(raspberryUserMerge);
        }else {
            raspberryUserMerge = raspberryUserMergeService.queryBySnCode(snCode);
            String user = raspberryUserMerge.getUserId();
            if(!userId.equals(user)){
                sendError();
                logger.info("该树莓派已经注册，不能两个账户同时用一个树莓派！！");
                logger.info("用户1ID=="+userId+"用户2ID=="+user+"正在使用");
                return;
            }
        }
        sendOK();
    }
    /**
     * 收到注销信息，进行注销操作
     */
    public void deregister(){
        //截取字符串得出 IMEI、树莓派ID，查询dev_config 是否是本公司的设备
        String context = content.replace("deregister:", "");
        String[] data1 = context.split(",");
        Map<String, String> map = new HashMap<String, String>();
        for (int i = 0; i < data1.length; i++) {
            String []data2 = data1[i].split(":");
            map.put(data2[0], data2[1]);
        }
        //IMEI从注册信息中截取出来
        String hashResult = map.get("hash_result");
        String exp = map.get("exp_id");
        String snCode = raspberryService.querySNByHashResult(hashResult);
        Integer proId = Integer.parseInt(exp);
        //设备下线: 这个产品下的所有设备都下线
        deviceService.updateOfflineByProId(proId);
        List<Device> deviceList = deviceService.queryByProId(proId);
        //解除绑定树莓派与用户的关系; 根据 userkey 查出user_id,删除 user_id、树莓派SN码;表raspberry_user_merge
        raspberryUserMergeService.deleteBySnCode(snCode);
        sendOK();
    }

    public void sendOK() {
        try {
            byte[] ok = {(byte) 0x4F, (byte) 0x4B};
            DatagramPacket  responseok = new DatagramPacket(ok, ok.length, packet.getAddress(), packet.getPort());
            socket.send(responseok);
            logger.info("********* send OK *************");
        } catch (IOException e) {
            e.printStackTrace();
        }


    }


    public void sendError() {
        try {
            byte[] error = {(byte) 0x55,(byte) 0x62,(byte) 0x62,(byte) 0x5f,(byte) 0x62};
                DatagramPacket  responseok = new DatagramPacket(error, error.length, packet.getAddress(), packet.getPort());
            socket.send(responseok);
            logger.info("********* Send Error *************");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
