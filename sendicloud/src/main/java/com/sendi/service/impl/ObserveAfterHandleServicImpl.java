package com.sendi.service.impl;

import com.sendi.entity.Device;
import com.sendi.service.UDPHandleService;
import com.sendi.utils.JsonUtil;
import com.sendi.utils.RedisUtil;
import com.sendi.utils.TransactionServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Map;

/**
 *
 * Observe收到的数据
 * Created by fengzm on 2019/2/2.
 */
@Service
public class ObserveAfterHandleServicImpl extends UDPHandleService {
    @Autowired
    private DataTypeServiceImpl dataTypeService;
    @Autowired
    private DeviceService deviceService;
    @Autowired
    private RaspberryService raspberryService;
    @Autowired
    private RedisUtil redisUtil;
    /**
     * 报文处理方法
     *
     * @param content
     * @param packet
     * @param socket
     */
    @Override
    public void doHandle(String content, DatagramPacket packet, DatagramSocket socket) {
        byte[] data = packet.getData();
        String msgID = data[2] + "" + data[3];
        if (!TransactionServer.observeQueue.isDuplication(msgID)) {
            TransactionServer.observeQueue.add(msgID);
            logger.info("第一次收到Observe 的msgID: " + msgID);
        } else {
            logger.info("重复收到Observe 的msgID: " + msgID + "不做处理");
            return;
        }
        if (!content.contains(str1) && !content.contains(str2)) {
            logger.info("========数据格式错误，不处理改数据: " + content);
            return;
        }
        Map<String, Object> map = JsonUtil.jsonToMap(content.substring(content.indexOf(str1) + 1, content.indexOf(str2) + 1));
        logger.info("数据报文:" + map);
        String hashResult = map.get(hashKey)+"";
        String snCode = raspberryService.querySNByHashResult(hashResult);

//        String IMEI = map.get(imeiKey) + "";
        String resName = map.get(nameKey) + "";
        Integer proId = Integer.parseInt(map.get(proKey).toString());
        String modId = map.get(moduleKey).toString();
        Device dev = deviceService.queryByProAndMod(proId, modId);
//        Device device = new Device();
//        device.setImei(IMEI);
//        device.setSnCode(snCode);
//        Device dev = deviceService.queryBySnCode(snCode);
        if(dev==null){
            logger.info("========设备不存在，不做处理: " + map);
            return;
        }
        BigInteger devId = dev.getId();
        logger.info("ObserveAfterHandleServicImpl  devId:"+devId+" msgID: "+msgID);
//        TransactionServer.obsMsgID.put(devId+""+resName,msgID);
        redisUtil.set(devId+""+resName,msgID,30);
        dataTypeService.doHandle(content,packet,socket);
    }
}
