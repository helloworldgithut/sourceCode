package com.sendi.service;

import com.sendi.utils.CoapServer;
import com.sendi.utils.MessageUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *TCP 处理数据报文抽象类
 * Created by fengzm on 2019/2/1.
 */
public abstract class UDPHandleService {
    protected Logger logger = LoggerFactory.getLogger(getClass());
    protected final String saveUrl = "http://192.168.60.137:8080/iot_aid/photo/show/import";
    protected final String userKey = "user_id",hashKey = "hash_result",proKey = "exp_id",moduleKey = "mod_id";
    protected final String nameKey = "name", unitKey = "unit", timeKey = "time", valueKey = "value", sortIdKey = "sort_id", flagKey = "flag";
    protected final String beginStr = "begin", endStr = "end", str1 = "[{", str2 = "}]";

    /**
     *报文处理方法
     * @param content
     * @param packet
     * @param socket
     */
    public abstract void doHandle(String content, DatagramPacket packet,DatagramSocket socket);

    /**
     * 发送COONOK，响应reok
     * @param packet
     */
    protected void conReok(DatagramPacket packet,DatagramSocket socket) {
        try {
            byte[] ok = {(byte) 0x43, (byte) 0x4F, (byte) 0x4E, (byte) 0x52, (byte) 0x45, (byte) 0x4F, (byte) 0x4B};
            DatagramPacket responseok = new DatagramPacket(ok, ok.length, packet.getAddress(), packet.getPort());
            socket.send(responseok);
            logger.info("========== send CONREOK ==========");
        } catch (IOException e) {
            logger.info("网络异常，send CONREOK fail");
            e.printStackTrace();
        }
    }
    /**
     *将数据封装成Map
     * @param content
     * @return
     */
    protected Map<String, String> packMap(String content) {
        Map<String, String> map = new HashMap<>();
        String[] akk = content.split(",");
        for (int i = 0; i < akk.length; i++) {
            String[] dataAck = akk[i].split(":");
            map.put(dataAck[0], dataAck[1]);
        }
        return map;
    }
    /**
     *
     *发送discover
     * @param high
     * @param low
     * @param packet
     */
    protected void coapDiscover(short high, short low, DatagramPacket packet,DatagramSocket socket) {
        try {
            byte[] data4 = {(byte) 0x40, (byte) 0x01, (byte) high, (byte) low, (byte) 0xbb,
                    (byte) 0x2E, (byte) 0x77, (byte) 0x65, (byte) 0x6C, (byte) 0x6C, (byte) 0x2D, (byte) 0x6B, (byte) 0x6E, (byte) 0x6F, (byte) 0x77, (byte) 0x6E, (byte) 0x04,
                    (byte) 0x63, (byte) 0x6F, (byte) 0x72, (byte) 0x65};
            DatagramPacket response = new DatagramPacket(data4, data4.length, packet.getAddress(), packet.getPort());
            socket.send(response);
            logger.info("==========  sent discover  ===========" + response);
        } catch (IOException e) {
            logger.info("网络异常，sent discover fail");
            e.printStackTrace();
        }
    }
    /**
     *
     * 发送obserce
     * @param res 资源名称
     * @param low
     * @param high
     * @param packet 数据包
     * @param socket
     */
    protected void observeRes(String res,short high,short low,DatagramPacket packet, DatagramSocket socket){
        int len;//整个数据报的长度
        try {
            byte []cc={(byte)0x40,(byte)0x01,(byte)high,(byte)low,(byte)0x60};//observe
            DatagramPacket response;
            byte []name = res.getBytes();//temperature
            byte []temp;                //中间数组，用来合并两个数组
            if(name.length<13){
                temp = new byte[cc.length+1];
                for(int i=0;i<cc.length;i++){
                    temp[i]=cc[i];
                }
                len=80+name.length;     //0x55 = 5*16 + 5
                byte n=(byte)len;
                temp[cc.length] = n;
                cc = temp;
                byte []resName = new byte[name.length+cc.length];
                System.arraycopy(cc, 0 ,resName, 0, cc.length);
                System.arraycopy(name, 0 ,resName, cc.length, name.length);
                response = new DatagramPacket(resName,resName.length, packet.getAddress(), packet.getPort());
                socket.send(response);
                logger.info("observe res <13 "+res);
            }else if(name.length>=13 && name.length<256) {
                temp = new byte[cc.length+2];
                for(int i=0;i<cc.length;i++){
                    temp[i]=cc[i];
                }
                len = 80+name.length;
                int num = len-93;
                byte n=(byte)num;
                temp[cc.length] = 93;
                temp[cc.length+1] = n;
                cc = temp;
                byte []resName = new byte[name.length+cc.length];
                System.arraycopy(cc, 0 ,resName, 0, cc.length);
                System.arraycopy(name, 0 ,resName, cc.length, name.length);
                response = new DatagramPacket(resName,resName.length, packet.getAddress(), packet.getPort());
                socket.send(response);
                logger.info("observe res >13 "+res);
                logger.info(Arrays.toString(resName));
            }else {
                logger.info("******资源名称过长！******");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送get 请求
     * @param resName
     * @param content
     * @param high
     * @param low
     */
    protected void sendGet(String resName,String content,short high,short low,DatagramPacket packet, DatagramSocket socket) {
        byte[] post = {(byte) 0x40, (byte) 0x01, (byte) high, (byte) low};
        byte[] name = resName.getBytes();
        byte  [] format = {(byte) 0x11, (byte) 0x00};
        byte[] flag = {(byte) 0xff};//
//        byte[] content1 = "op,tp".getBytes();
        byte[] content1 = content.getBytes();
        byte[] payload = new byte[content1.length + 1];
        System.arraycopy(flag, 0, payload, 0, flag.length);
        System.arraycopy(content1, 0, payload, flag.length, content1.length);
        byte[] sendData = new byte[0];
        try {
            sendData = MessageUtil.packData(post, name, payload, format);
        } catch (Exception e) {
            e.printStackTrace();
        }
        DatagramPacket response = new DatagramPacket(sendData,sendData.length, packet.getAddress(), packet.getPort());
        try {
            socket.send(response);
            logger.info("发送get 请求："+ new String(sendData));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * unicodeToString
     * @param str
     * @return
     */
    public  String unicodeToString(String str) {
        Pattern pattern = Pattern.compile("(\\\\u(\\p{XDigit}{4}))");
        Matcher matcher = pattern.matcher(str);
        char ch;
        while (matcher.find()) {
            ch = (char) Integer.parseInt(matcher.group(2), 16);
            str = str.replace(matcher.group(1), ch+"" );
        }
        return str;
    }

    /**
     *发送空请求
     * @param high
     * @param low
     * @param packet
     * @param socket
     */

    public void emptyGet(short high, short low, DatagramPacket packet, DatagramSocket socket) {
        try {
            DatagramPacket response;
            byte[] data3 = {(byte) 0x60, (byte) 0x00, (byte) high, (byte) low};
            response = new DatagramPacket(data3, data3.length, packet.getAddress(), packet.getPort());
            socket.send(response);
            //发完一条messageID增加1
            CoapServer.changeMessageID();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
