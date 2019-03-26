package com.exam.cloud;

import org.springframework.boot.CommandLineRunner;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;


public class TcpThread implements CommandLineRunner {
    public static Map<String, Socket> socketMap = new HashMap<>();

    @Override
    public void run(String... args) throws Exception {
//        Thread ttt = new Thread( new TcpThread());
//        ttt.start();
        receive();
    }

    public void receive () {
        String receive = null;
        int count = 0;
        try {
            ServerSocket server = new ServerSocket(8088);
            while (true) {
                Socket socket = server.accept();
                socketMap.put("111",socket);

                count++;//客户端数量增加
                System.out.println("客户端数量为:" + count);
                InetAddress address = socket.getInetAddress();
                int port = socket.getPort();
                System.out.println("客户端IP：" + address.getHostAddress() + "" + address + "  port:" + port);
                //更新设备状态；添加IP ：端口

                InputStream ins = socket.getInputStream();
                OutputStream out = socket.getOutputStream();
//                int port = socket.getPort();
                byte[] buff = null;
                while (true) {
                    buff = new byte[2048];
                    int r = ins.read(buff);
                    String receive2 = new String(buff);
                    System.out.println(receive2);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (receive != null) {
            System.out.println("receive empty");
        }
    }


}
