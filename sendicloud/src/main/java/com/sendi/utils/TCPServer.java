package com.sendi.utils;

import com.sendi.service.impl.DeviceService;
import com.sendi.service.impl.RaspberryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

@Component
public class TCPServer implements Runnable{
    private static Logger logger = LoggerFactory.getLogger(TCPServer.class);
    public static Map<String, String> contentMap = new HashMap<>();//保存收到信息
    public static Map<String, Socket> socketMap = new HashMap<>(); //保存连接的socket
    @Autowired
    RaspberryService raspberryService;
    @Autowired
    DeviceService deviceService;

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

//            while(true){
//                Socket socket = server.accept();
//                InputStream is = socket.getInputStream();
//                byte[] buff = new byte[1024];
//
//                InetAddress address = socket.getInetAddress();
//                int port = socket.getPort();
//                while (-1!=(is.read(buff))){
//                     receive = new String(buff);
//                    System.out.println("IP:"+address+" PORT:"+port+"content:"+receive);
//                }
//                is.close();
//                socket.close();
//            }
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
        for(String key : socketMap.keySet()){
//            long result = d - lifeMap.get(key);
//            logger.info(key+ " : "+lifeMap.get(key)+"  result "+result);
//            Device device = deviceService.queryById(key);
//            if(result > 150000){
//                deviceService.updateStateById(device);
//                logger.info("已更新状态为离线的设备"+device);
//            }
            String snCode = raspberryService.querySNByHashResult(key);
            logger.info("每30s刷新一次"+snCode);
            logger.info("socketMap.Size"+socketMap.size()+"keySet");
            if(socketMap.get(key) == null){
                deviceService.updateOfflineBySnCode(snCode);
                logger.info("设备"+snCode+" 已下线");
            }
            if (socketMap.size()==0){
                deviceService.updateOfflineBySnCode(snCode);
                logger.info("ssssssssss设备"+snCode+" 已下线");
            }
        }
    }
}
