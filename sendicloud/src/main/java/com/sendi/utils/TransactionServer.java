package com.sendi.utils;

import com.sendi.service.UDPHandleService;
import com.sendi.service.impl.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class TransactionServer implements Runnable {
    private Logger logger = LoggerFactory.getLogger(getClass());
    private final static int PORT = 5687;            //udp接收端口
    public static DatagramSocket socket;
    public static Map<String, String> sendGetObsMsgID = new HashMap<>();
    public static Map<String, String> revGetObsMsgID = new HashMap<>();
    public static List<String> revGetMsgID = new ArrayList<>();
    public static List<String> sendGetMsgID = new ArrayList<>();
    public static Map<Integer, Class<? extends UDPHandleService>> handleClassMap = new HashMap<>();
    public static LoopQueue discoverQueue = new LoopQueue();
    public static LoopQueue observeQueue = new LoopQueue();

    static {
        handleClassMap.put(1, AckHandleServicImpl.class);
        handleClassMap.put(2, ResourceHandleServicImpl.class);
        handleClassMap.put(3, AutoDataHandleServicImpl.class);
        handleClassMap.put(4, ObserveAfterHandleServicImpl.class);
        handleClassMap.put(5, GetAfterHandleServicImpl.class);
        handleClassMap.put(6, ResBlockServicImpl.class);
    }

    @Override
    public void run() {
        logger.info("Transation Server started *******************");
        receive();
        logger.info("Transation Server  stop ***************");
    }
    /**
     * udp socket连接设备，接收信息
     */
    public String receive() {
        try {
            socket = new DatagramSocket(PORT);
        } catch (SocketException e) {
            e.printStackTrace();
        }
        byte[] data;//创建字节数组，指定接收的数据包的大小
        DatagramPacket packet;
        while (true) {
            data = new byte[1358];
            packet = new DatagramPacket(data, data.length);
            try {
                socket.receive(packet);//此方法在接收到数据报之前会一直阻塞
            } catch (IOException e) {
                e.printStackTrace();
            }
            new Thread(new UDPHandleThread(socket, packet)).start();

        }
    }
}

