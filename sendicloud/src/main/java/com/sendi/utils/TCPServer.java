package com.sendi.utils;

import com.alibaba.fastjson.JSON;
import com.sendi.entity.Device;
import com.sendi.service.impl.DeviceService;
import com.sendi.service.impl.RaspberryService;
import com.sendi.service.impl.RaspberryUserMergeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

@Component
public class TCPServer implements Runnable{
    private static Logger logger = LoggerFactory.getLogger(TCPServer.class);
    public static Map<BigInteger, Long> aliveMap = new HashMap<>();//保存收到信息
    public static Map<String, Socket> socketMap = new HashMap<>(); //保存连接的socket
    @Autowired
    RaspberryService raspberryService;
    @Autowired
    DeviceService deviceService;
    @Autowired
    RaspberryUserMergeService raspberryUserMergeService;
    @Override
    public void run() {
        receive();
        System.out.println("TCP stop =====================");
    }

    /**
     * 设备通过TCP协议连接上来，
     * */
    public void receive (){
        String receive =null;
        int count=0;
        try {
            ServerSocket server = new ServerSocket(8001);
            while(true){
                Socket socket =server.accept();
                new TCPServerThread(socket).start();
                count++;//客户端数量增加
                System.out.println("客户端数量为:" + count);
                InetAddress address = socket.getInetAddress();
                int port = socket.getPort();
                System.out.println("客户端IP：" + address.getHostAddress()+""+address+"  port:"+port);
                //更新设备状态；添加IP ：端口
            }
        }catch (IOException e) {
            e.printStackTrace();
        }
        if(receive != null){
            System.out.println("receive empty");
        }
    }

    @Scheduled(cron = "*/30 * * * * *")
    public void scheduled2(){
        long d = System.currentTimeMillis();
        if(aliveMap.size()>0){
            for (BigInteger key : aliveMap.keySet()) {
                long result = d - aliveMap.get(key);
//                    logger.info("30s检查一次"+key + " : " + lifeMap.get(key) + "  result " + result);
                Device device = deviceService.queryById(key);
//                String snCode = raspberryService.querySNByHashResult(key);
                if (result > 200000) {
                    if(device != null){
                        if(device.getState()==0){
                            aliveMap.remove(key);
                        }else {
                            //超时设备下线
                            deviceService.updateOfflineById(device.getId());
                            Integer proId = device.getProId();
                            int num = deviceService.queryOnlineByProId(proId);
                            logger.info("已更新状态为离线的RTMP设备,timeout:"+result+",下线设备ID："+device.getId());
                            aliveMap.remove(key);
                            HTTPUtil.getResponse(device.getId(),new BigInteger("0"));

                            if(num == 0){
                                String snCode = device.getSnCode();
                                //根据 userkey 查出user_id,删除 user_id、树莓派SN码  到 表raspberry_user_merge
                                raspberryUserMergeService.deleteBySnCode(snCode);
                                logger.info("RTMP设备全部下线，删除用户与树莓派的关联："+snCode);
                                aliveMap.remove(key);
                            }
                        }
                    }
                }
            }
        }
    }
}
