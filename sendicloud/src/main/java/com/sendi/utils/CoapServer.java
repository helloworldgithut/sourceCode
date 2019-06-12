package com.sendi.utils;

import com.alibaba.fastjson.JSON;
import com.sendi.entity.Device;
import com.sendi.service.impl.DeviceService;
import com.sendi.service.impl.ProductServiceImpl;
import com.sendi.service.impl.RaspberryService;
import com.sendi.service.impl.RaspberryUserMergeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.BigInteger;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class CoapServer implements Runnable {
    private Logger logger = LoggerFactory.getLogger(CoapServer.class);
    public static short msg_id_high = 0;     //messageID由2个byte 组成，第一个字节
    public static short msg_id_low = 0;       //第二个字节
    public static Map<BigInteger, Long> lifeMap = new HashMap<>();// 用来判断设备的在/离线状态
    private final int PORT = 5686;            //udp接收端口
    private DatagramSocket socket;
    private DatagramPacket packet;
    @Autowired
    private DeviceService deviceService;
    @Autowired
    private ProductServiceImpl productService;
    @Autowired
    private RaspberryService raspberryService;
    @Autowired
    private RaspberryUserMergeService raspberryUserMergeService;

    @Override
    public void run() {
        logger.info("Register Server started *******************");
        receive();
        logger.info("Register Server  stop ***************");
    }
    /**
     * udp socket连接设备，接收信息
     */
    public void receive() {
        try {
            socket = new DatagramSocket(PORT);
        } catch (SocketException e) {
            e.printStackTrace();
        }
        byte[] data = new byte[1024];//创建字节数组，指定接收的数据包的大小
        while (true) {
            packet = new DatagramPacket(data, data.length);
            try {
                socket.receive(packet);//此方法在接收到数据报之前会一直阻塞
                logger.info("有设备向注册服务器，发起注册/注销");
                //开启线程处理
                new  Thread(new CoapHandleThread(socket,packet, productService, raspberryService,
                        raspberryUserMergeService,deviceService)).start();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    @Scheduled(cron ="*/30 * * * * *" )
    public void scheduled() {
        long d = System.currentTimeMillis();
        if(lifeMap.size()>0){
            try {
                for (BigInteger key : lifeMap.keySet()) {
                    long result = d - lifeMap.get(key);
//                    logger.info("30s检查一次"+key + " : " + lifeMap.get(key) + "  result " + result);
                    Device device = deviceService.queryById(key);
                    if (result > 200000) {
                        if(device != null){
                            if(device.getState()==0){
                                lifeMap.remove(key);
                            }else {
                                //超时设备下线
                                deviceService.updateOfflineById(device.getId());
                                //添加上线通知
                                HTTPUtil.getResponse(device.getId(),new BigInteger("0"));
                                Integer proId = device.getProId();
                                int num = deviceService.queryOnlineByProId(proId);
                                logger.info("已更新状态为离线的COAP设备,timeout:"+result+",下线设备ID："+device.getId());
                                lifeMap.remove(key);

                                if(num == 0){
                                    String snCode = device.getSnCode();
                                    //根据 userkey 查出user_id,删除 user_id、树莓派SN码  到 表raspberry_user_merge
                                    raspberryUserMergeService.deleteBySnCode(snCode);
                                    logger.info(snCode+"该树莓派上的设备全部下线");
                                        lifeMap.remove(key);
                                }
                            }
                        }
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
    /**
     * msg_id 加1
     * @return mgs_id
     */
    public static synchronized List<Short> changeMessageID() {
        List<Short> list = new ArrayList<>(2);
        msg_id_low++;
        if (msg_id_low > (short) 127) {
            msg_id_high++;
            msg_id_low = 0;
        }
        if (msg_id_high > (short) 127) {
            msg_id_high = 0;
        }
        list.add(msg_id_high);
        list.add(msg_id_low);
        return list;
    }


}
