package com.sendi.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

/**
 * Created by fengzm on 2019/1/31.
 */
public class UDPHandleThread implements Runnable {
    private Logger logger = LoggerFactory.getLogger(getClass());
    private DatagramSocket socket;
    private DatagramPacket packet;
    private RedisUtil redisUtil = ApplicationContextProvider.getBean(RedisUtil.class);

    public UDPHandleThread(DatagramSocket socket, DatagramPacket packet) {
        this.socket = socket;
        this.packet = packet;
    }

    @Override
    public void run() {
        byte[] data = packet.getData();
        String content = new String(packet.getData(), 0, packet.getLength()); //拼接字符串, 收到的数据
        logger.info("udp服务器接收到数据:" + content);
        logger.info("当前客户端的IP、PORT为：" + packet.getAddress().getHostAddress() + ":" + packet.getPort());
        int type = 0;
        if (data[0] == 97 && data[1] == 99 && data[2] == 107) {
            //收到 reok0/1
            type = 1;
        } else if (data[1] == (byte) 0x45 && data[4] == (byte) 0xc1 && data[5] == (byte) 0x28) {
            //收到的资源 0x60 0x45 [] [] 0xc1 0x28
            type = 2;
        } else if (data[0] == (byte) 0x50 && data[1] == (byte) 0x45) {
            //收到设备自动上传的数据 0x50 0x45
            type = 3;
        } else if (data[0] == (byte) 0x60 && data[1] == (byte) 0x45 && data[4] == (byte) 0x61 && data[6] == (byte) 0x61) {
            // Observe/请求后，收到的数据
            type = 4;
        } else if (data[0] == (byte) 0x60 && data[1] == (byte) 0x45 && data[4] == (byte) 0xc1 && data[5] == (byte) 0x32) {
            //GET请求后，收到的数据
            type = 5;
        } else if (data[0] == (byte) 0x60 && data[1] == (byte) 0x44) {
            //post/put
            logger.info("Post/Put 的响应MsgID:"+data[2]+""+data[3]);
//            TransactionServer.postMsgID.put(data[2] + "" + data[3], packet);
            redisUtil.set(data[2] + "" + data[3], content, 30);

            logger.info("Post/Put 的响应" + content);
        } else {
            logger.info("未定义数据");
        }
        logger.info(""+type);
        if (TransactionServer.handleClassMap.containsKey(type)) {
            //开始处理数据
            ApplicationContextProvider.getBean(TransactionServer.handleClassMap.get(type)).doHandle(content, packet, socket);
        }

    }


}


