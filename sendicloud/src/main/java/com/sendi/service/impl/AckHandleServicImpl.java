package com.sendi.service.impl;

import com.sendi.service.UDPHandleService;
import com.sendi.utils.CoapServer;
import com.sendi.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.List;
import java.util.Map;

/**
 *
 * Created by fengzm on 2019/2/1.
 */

@Service
public class AckHandleServicImpl extends UDPHandleService {
    @Autowired
    private DeviceService deviceService;
    @Autowired
    private RaspberryService raspberryService;
    @Autowired
    private RedisUtil redisUtil;

    private final  String ack = "ack", reok0 = "reok0", reok1 = "reok1";
    /**
     *  收到reok
     * @param content
     * @param packet
     * @param socket
     */
    @Override
    public void doHandle(String content, DatagramPacket packet,DatagramSocket socket) {

        String[] info = content.split(":");
        if (ack.equals(info[0])) {
            Map<String, String> map = packMap(content);
            String hashResult = map.get(hashKey);
            String snCode = raspberryService.querySNByHashResult(hashResult);
//            String discoverKey = map.get(userKey) + map.get(proKey);
            String discoverKey = hashResult + map.get(proKey);
            //
            logger.info("snCode="+snCode+"discoverKey="+discoverKey);
//            Device device = new Device();
////            device.setImei(map.get(imeiKey));
//            device.setSnCode(snCode);
//            Device dev = deviceService.queryBySnCode(snCode);
//            BigInteger devId = dev.getId();
//            dev.setIp(packet.getAddress().toString().replace("/", ""));
//            dev.setPort(packet.getPort());
//            dev.setAcceptTime(new Timestamp(new Date().getTime()));
//            deviceService.updateBySnCode(dev);
//            CoapServer.lifeMap.put(devId, System.currentTimeMillis());
            String reqAck = map.get(ack);
            if (reok0.equals(reqAck)) {
                conReok(packet,socket);
                sentDiscover(packet,discoverKey,socket);
            }else if(reok1.equals(reqAck)){
                conReok(packet,socket);
            }else {
                logger.info("Error ACK!");
                logger.info("Error content:"+content);
            }
        }
    }
    /**
     * 发送Discover 给设备
     * @param packet
     * @param devId
     */
//    public void sentDiscover(DatagramPacket packet,BigInteger devId,DatagramSocket socket){
//        List<Short> list = CoapServer.changeMessageID();
//        short high = list.get(0), low = list.get(1);
//        coapDiscover(high, low, packet,socket);
//        String msgIdStr = high + "" + low;
//        //缓存msgID对应的devID
//        TransactionServer.devID.put(msgIdStr,devId);
//        int count = 1;
//        for (;;) {
//            try {
//                Thread.sleep(3000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            String dicFail = TransactionServer.discoverID.get(devId + "");
//            logger.info("++++++Discover 发送   key++++++++" + devId + "==MsgID=" + msgIdStr);
//            logger.info("++++++Discover 缓冲区 key++++++++" + devId + "==MsgID=" + dicFail);
//            if (msgIdStr.equals(dicFail)) {
//                break;
//            } else {
//                if (count >= 3) {
//                    break;
//                }
//                //重发
//                coapDiscover(high, low, packet,socket);
//                count++;
//            }
//        }
//    }
    /**
     * 发送Discover 给树莓派
     * @param packet
     */
    public void sentDiscover(DatagramPacket packet, String discoverKey, DatagramSocket socket){
        List<Short> list = CoapServer.changeMessageID();
        short high = list.get(0), low = list.get(1);
        coapDiscover(high, low, packet,socket);
        String msgIdStr = high + "" + low;
        //缓存msgID对应的信息
//        TransactionServer.ResponID.put(msgIdStr,discoverKey);
        redisUtil.set(msgIdStr,discoverKey,30);
        int count = 1;
        for (;;) {
            String dicFail = null;
            try {
                Thread.sleep(3000);
                dicFail = redisUtil.get(discoverKey).toString();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }catch (NullPointerException e){
                logger.info("redisUtil缓存中没有discoverKey=="+discoverKey);
            }
//            String dicFail = TransactionServer.discoverID.get(discoverKey);
            logger.info("++++++Discover 发送   key++++++++" + discoverKey + "==MsgID=" + msgIdStr);
            logger.info("++++++Discover 缓冲区 key++++++++" + discoverKey + "==MsgID=" + dicFail);
            if (msgIdStr.equals(dicFail)) {
                break;
            } else {
                if (count >= 3) {
                    break;
                }
                //重发
                coapDiscover(high, low, packet,socket);
                count++;
            }
        }
    }

}
