package com.sendi.service.impl;

import com.alibaba.fastjson.JSON;
import com.sendi.dao.DeviceDaoI;
import com.sendi.entity.Device;
import com.sendi.entity.DeviceInstructions;
import com.sendi.service.DeviceInstructService;
import com.sendi.utils.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigInteger;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/***
    * @Author Mengfeng Qin
    * @Description COAP协议接入的设备指令下发逻辑接口
    * @Date 2019/3/29 11:12
*/
@Service
public class DeviceInstructServiceImpl implements DeviceInstructService {
    private Logger logger = LoggerFactory.getLogger(getClass());
    private final int countNum = 2;
    @Autowired
    private DeviceDaoI deviceDaoI;
    @Autowired
    private RedisUtil redisUtil;
    /**
     * 读取
     * @param deviceInstructions
     * @return
     * @throws Exception
     */
    @Override
    public ResponseData readResource(DeviceInstructions deviceInstructions) throws Exception {
        int count = 0;
        List<Short> list = CoapServer.changeMessageID();
        short high = list.get(0), low = list.get(1);
        byte[] name = deviceInstructions.getResName().getBytes();
        byte[] get = {(byte) 0x40, (byte) 0x01, (byte) high, (byte) low};
        byte[] resName = MessageUtil.packData(get, name);
        for (; ; ) {

            //发送拼接好的读取指令
            ResponseData result = sendData(deviceInstructions, resName);
            if (result != null) {
                return result;
            }
            String getMsg = deviceInstructions.getDevId() + "" + deviceInstructions.getResName() + high + "" + low;
//            Thread.sleep(5000);
            for(int i=0 ;i<2;i++){
                Thread.sleep(2000);
                if(redisUtil.hasKey(getMsg)) {
                    logger.info("write收到回复：" + deviceInstructions);
                    return ResponseData.success(null);

                }
            }
//            logger.info("====redisUtil.hasKey():"+redisUtil.hasKey(getMsg));
            if (redisUtil.hasKey(getMsg)) {
                logger.info("read 收到回复：" + deviceInstructions);
                //加空请求
//                emptyGet(high, low, packet, socket);
                String content = redisUtil.get(getMsg).toString();
                int start = content.indexOf("[");
                int stop = content.indexOf("]");
                content = content.substring(start + 1, stop);
                return ResponseData.success(content);
            } else {
                logger.info("read重发：" + deviceInstructions);

                if (count > countNum) {
                    logger.info("read超过重发次数，重发结束：" + deviceInstructions);
                    break;
                }
                count++;
            }
        }
        return ResponseData.fail("read操作失败，请重新操作");
    }
    /**
     * 写入
     * @param deviceInstructions
     * @return
     * @throws Exception
     */
    @Override
    public ResponseData writeResource(DeviceInstructions deviceInstructions) throws Exception {
        int count = 0;
        List<Short> list = CoapServer.changeMessageID();
        short high = list.get(0), low = list.get(1);
        byte[] put = {(byte) 0x40, (byte) 0x03, (byte) high, (byte) low};
        byte[] name = deviceInstructions.getResName().getBytes();
        byte[] format = {(byte) 0x11, (byte) 0x00};
        byte[] flag = {(byte) 0xff};
        byte[] content1 = deviceInstructions.getContent().getBytes();
        byte[] payload = new byte[content1.length + 1];
        System.arraycopy(flag, 0, payload, 0, flag.length);
        System.arraycopy(content1, 0, payload, flag.length, content1.length);
        byte[] resName = MessageUtil.packData(put, name, payload, format);
        for ( ; ; ) {

            //发送拼接好的写入指令
            ResponseData result = sendData(deviceInstructions, resName);
            if (result != null) {
                return result;
            }
            String getMsg = high + "" + low;
//            logger.info("=========Write　getMsg======"+getMsg+":"+redisUtil.get(getMsg));
//            Thread.sleep(8000);
            for(int i=0 ;i<2;i++){
                Thread.sleep(2000);
                if(redisUtil.hasKey(getMsg)) {
                    logger.info("write收到回复：" + deviceInstructions);
                    return ResponseData.success(null);

                }
            }
            if(redisUtil.hasKey(getMsg)){
                logger.info("write收到回复：" + deviceInstructions);
                return ResponseData.success(null);
            } else {
                logger.info("write重发：" + deviceInstructions);
                if (count > countNum) {
                    logger.info("write超过重发次数，重发结束：" + deviceInstructions);
                    break;
                }
                count++;
            }
        }
        return ResponseData.fail("write操作失败，请重新操作");
    }

    /**
     * 执行
     * @param deviceInstructions
     * @return
     * @throws Exception
     */
    @Override
    public ResponseData excuteResource(DeviceInstructions deviceInstructions) throws Exception {
        int count = 0;
        List<Short> list = CoapServer.changeMessageID();
        short high = list.get(0), low = list.get(1);
        byte[] post = {(byte) 0x40, (byte) 0x02, (byte) high, (byte) low};
        byte[] name = deviceInstructions.getResName().getBytes();
        byte  [] format = {(byte) 0x11, (byte) 0x00};
        byte[] flag = {(byte) 0xff};
        byte[] content1 = deviceInstructions.getContent().getBytes();
        byte[] payload = new byte[content1.length + 1];
        System.arraycopy(flag, 0, payload, 0, flag.length);
        System.arraycopy(content1, 0, payload, flag.length, content1.length);
        byte[] resName = MessageUtil.packData(post, name, payload, format);
        for (; ; ) {

            //发送拼接好的执行指令
            ResponseData result = sendData(deviceInstructions, resName);
            if (result != null) {
                return result;
            }
            String getMsg = high + "" + low;
//            logger.info("=========Excute　getMsg======"+getMsg);
            for(int i=0 ;i<2;i++){
                Thread.sleep(2000);
                if(redisUtil.hasKey(getMsg)) {
                    logger.info("write收到回复：" + deviceInstructions);
                    return ResponseData.success(null);

                }
            }
//            Thread.sleep(8000);
            if(redisUtil.hasKey(getMsg)){
                logger.info("excute收到回复：" + deviceInstructions);
                return ResponseData.success(null);
            } else {
                logger.info("excute重发：" + deviceInstructions);
                if (count > countNum) {
                    logger.info("excute超过重发次数，重发结束：" + deviceInstructions);
                    break;
                }
                count++;
            }
        }
        return ResponseData.fail("excute操作失败，请重新操作");
    }
    /**
     * 发送指令的公共方法
     * @param deviceInstructions   设备参数
     * @param resName  下发数据包
     * @return
     */
    public ResponseData sendData(DeviceInstructions deviceInstructions, byte[] resName) {
        InetAddress ip ;
        DatagramPacket response ;
        Device device ;
        //选出要连接的设备
        device = deviceDaoI.queryById(deviceInstructions.getDevId());
        if (device == null) {
            logger.info("设备信息获取失败，返回设备不在线：" + deviceInstructions);
             deviceDaoI.updateOfflineById(deviceInstructions.getDevId());
            //添加上线通知
            HTTPUtil.getResponse(device.getId(),new BigInteger("0"));
            return ResponseData.fail("设备不在线");
        }
//        logger.info("======"+getClass() +"====DeviceInstructServiceImpl 设备信息" + device);
        //获取IP和端口
        String ipp = device.getIp();
        int port = device.getPort();
        try {
            ip = InetAddress.getByName(ipp);
            response = new DatagramPacket(resName, resName.length, ip, port);
            TransactionServer.socket.send(response);
        } catch (Exception e) {
            logger.info("socket或ip获取失败，返回设备不在线：" + deviceInstructions);
            logger.info(e.getMessage());
            deviceDaoI.updateOfflineById(deviceInstructions.getDevId());
            //添加上线通知
            HTTPUtil.getResponse(device.getId(),new BigInteger("0"));
            return ResponseData.fail("设备离线");
        }
        logger.info("=============设备发送：" + deviceInstructions);
        return null;
    }
}
