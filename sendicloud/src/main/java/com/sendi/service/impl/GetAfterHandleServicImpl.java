package com.sendi.service.impl;

import com.sendi.entity.Device;
import com.sendi.service.UDPHandleService;
import com.sendi.utils.JsonUtil;
import com.sendi.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Map;

/**
 * Created by fengzm on 2019/2/2.
 */
@Service
public class GetAfterHandleServicImpl extends UDPHandleService {
    private final String beginStr = "begin", endStr = "end", str1 = "[{", str2 = "}]";
    @Autowired
    private DeviceService deviceService;
    @Autowired
    private DataTypeServiceImpl dataTypeService;
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
        if (!content.contains(str1) && !content.contains(str2)) {
            logger.info("========GetAfterHandleServiceImpl数据格式错误，不处理改数据: " + content);
            return;
        }
        byte[] data = packet.getData();
        String pack = new String(data);
        Map<String, Object> map = JsonUtil.jsonToMap(content.substring(content.indexOf(str1) + 1, content.indexOf(str2) + 1));
        logger.info("GetAfterHandleServiceImpl数据报文:" + map);
        String hashResult = map.get(hashKey)+"";
        String snCode = raspberryService.querySNByHashResult(hashResult);
        Integer proId = Integer.parseInt(map.get(proKey).toString());
        String modId = map.get(moduleKey).toString();
//        String IMEI = map.get(imeiKey) + "";
        String resName = map.get(nameKey) + "";

        Device dev = deviceService.queryByProAndMod(proId, modId);

//        Device dev = deviceService.queryBySnCode(snCode);
        if(dev==null){
            logger.info("========GetAfterHandleServicImpl 设备不存在，不做处理: " + map);
            return;
        }
//        else {
//            BigInteger devId = dev.getId();
//            Device device = new Device();
//            device.setSnCode(snCode);
//            deviceService.updateById(device);
//        }
        String key = dev.getId() + "" + resName + data[2] + "" + data[3];
//        TransactionServer.readMsgID.put(key, packet);
        redisUtil.set(key,pack,30);



        dataTypeService.doHandle(content,packet,socket);





    }



}
