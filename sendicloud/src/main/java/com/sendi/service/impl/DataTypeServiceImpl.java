package com.sendi.service.impl;

import com.alibaba.fastjson.JSON;
import com.sendi.config.Msg;
import com.sendi.config.TriggerPool;
import com.sendi.entity.*;
import com.sendi.service.DataTypeService;
import com.sendi.service.UDPHandleService;
import com.sendi.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by fengzm on 2019/2/2.
 */
@Service
public class DataTypeServiceImpl  extends UDPHandleService implements DataTypeService {

    @Autowired
    private DeviceService deviceService;
    @Autowired
    private ResourceService resourceService;
    @Autowired
    private NumberDataService numberDataService;
    @Autowired
    private StringDataService stringDataService;
    @Autowired
    private ImageDataService imageDataService;
    @Autowired
    private DeviceInstructServiceImpl deviceInstructService;
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
            logger.info("========数据格式错误，不处理改数据: " + content);
            logger.info("IP　：　PORT　"+packet.getAddress()+":"+packet.getPort());
            return;
        }
        Map<String, Object> map = JsonUtil.jsonToMap(content.substring(content.indexOf(str1) + 1, content.indexOf(str2) + 1));
        logger.info("数据报文:" + map);
        logger.info("IP　：　PORT　"+packet.getAddress()+":"+packet.getPort());
        String hashResult = map.get(hashKey)+"";
        String snCode = raspberryService.querySNByHashResult(hashResult);
//        String IMEI = map.get(imeiKey) + "";//换成hashResult
        String resName = map.get(nameKey) + "";
        String type = map.get(Msg.TYPE_KEY) + "";
        Integer proId = Integer.parseInt(map.get(proKey).toString());
        String modId = map.get(moduleKey).toString();
        Device device = new Device();
//        device.setImei(IMEI);//换成SN码
        device.setSnCode(snCode);
        device.setPort(packet.getPort());
        String ip = packet.getAddress().toString().replace("/", "");
        device.setIp(ip);
        device.setProId(proId);
        device.setModId(modId);
        //查询设备存不存在
        Device dev = deviceService.queryByProAndMod(proId, modId);
        if(dev==null){
            logger.info("========设备不存在，不做处理: " + map);
            return;
        }
        //判断设备在不在线
        if(dev.getState()==0){
            logger.info("设备不在线：state = "+dev.getState());
            return;
        }
        //更新设备IP 、port 、sn码
        deviceService.updateByProAndMod(device);
        BigInteger devId = dev.getId();
        Resource res = new Resource();
        res.setDevId(devId);
        res.setResName(resName);
        Resource queryRes = resourceService.queryByDevIDAndName(res);
        logger.info("===========查询RESOURCE信息 :" + queryRes);
        if(queryRes==null){
            logger.info("========资源不存在，不做处理: " + map);
            return;
        }
        Integer resId = queryRes.getId();
        Long uptime = System.currentTimeMillis();
        CoapServer.lifeMap.put(devId, uptime);
        switch (type) {
            case Msg.TYPE_00:
                break;
            case Msg.TYPE_01:
                this.handleType01(map, devId, resId);
                break;
            case Msg.TYPE_02:
                this.handleType02(map, devId, resId);
                break;
            case Msg.TYPE_03:
                this.handleType03(map, devId, resId, packet, socket);
                break;
            case Msg.TYPE_04:
                this.handleType04(map, devId, resId, packet, socket);
                break;
            default:
                logger.info("该数据类型未定义，请检查数据类型");
        }
    }
    /**
     * 数据类型为数值类型
     * @param map
     * @param devId
     * @param resId
     */
    @Override
    public void handleType01(Map<String, Object> map, BigInteger devId, Integer resId) {
        String resName = map.get(nameKey) + "";
        String unit0 = map.get(unitKey) + "";
        String inTime = map.get(timeKey) + "";
        String value1 = map.get(valueKey) + "";
        Integer proId = Integer.parseInt(map.get(proKey).toString());
        Double value = Double.parseDouble(value1);
        BigDecimal val = BigDecimal.valueOf(value);
        //数据类型为数值类型
        String unit = unicodeToString(unit0);
        logger.info("================unit0 = " + unit0 + "   unit = " + unit);
        NumberData numdata = new NumberData();
        numdata.setDevId(devId);
        numdata.setValued(value);
        numdata.setResId(resId);
        numdata.setUnit(unit);
        numdata.setProId(proId);
        Long lon = Long.parseLong(inTime);
        Timestamp time = new Timestamp(lon);
        logger.info("设备端的发送时间：" + time);
        numdata.setSendTime(time);
        logger.info(devId + " , " + resName + " , " + value + " , " + time);
        numberDataService.addData(numdata);
        logger.info("添加数据成功");
        //todo 触发器数据过滤处理,设备在线只触发一次，map缓存
//        if(TriggerPool.isExecute.containsKey(resId)){
        if(TriggerPool.triggers.containsKey(resId)){
            TriggerPool.thresholdFilter(resId,val);
        }

    }

    /**
     * 数据类型为字符串类型
     *
     * @param map
     * @param devId
     * @param resId
     */

    @Override
    public void handleType02(Map<String, Object> map, BigInteger devId, Integer resId) {
        String resName = map.get(nameKey) + "";
        String unit0 = map.get(unitKey) + "";
        String inTime = map.get(timeKey) + "";
        String value = map.get(valueKey) + "";
        Integer proId = Integer.parseInt(map.get(proKey).toString());
        //数据类型为字符串类型
        String unit = unicodeToString(unit0);
        logger.info("================unit0 = " + unit0 + "   unit = " + unit);
        StringData strData = new StringData();
        strData.setDevId(devId);
        strData.setValued(value);
        strData.setResId(resId);
        strData.setUnit(unit);
        strData.setProId(proId);
        Long lon = Long.parseLong(inTime);
        Timestamp time = new Timestamp(lon);
        logger.info("设备端的发送时间：" + time);
        strData.setSendTime(time);
        logger.info(devId + " , " + resName + " , " + value + " , " + time);
        stringDataService.addData(strData);
        logger.info("添加数据成功");
    }


    /**
     * 数据类型为图片类型器 0x50
     * @param map
     * @param devId
     * @param resId
     * @param packet
     * @param socket
     */
    public void handleType03(Map<String, Object> map, BigInteger devId, Integer resId, DatagramPacket packet, DatagramSocket socket) {
        byte[] data = packet.getData();
        short high = data[2], low = data[3];
        String resName = map.get(nameKey) + "";
        String inTime = map.get(timeKey) + "";
        String value = map.get(valueKey) + "";
        String sortId = map.get(sortIdKey) + "";
        String flag = map.get(flagKey) + "";
        //数据类型为图片类型器 0x50
        if (beginStr.equals(value)) {
            logger.info("收到开始标志begin------改图片的总片段数为：" + Integer.parseInt(sortId));
            DeviceInstructions deviceInstructions = new DeviceInstructions();
            deviceInstructions.setDevId(devId);
            deviceInstructions.setResName(resName);
            deviceInstructions.setContent("beginok");
            sendEnd(deviceInstructions);
            logger.info("=====send beginok");
        } else if (endStr.equals(value)) {
            //查image_data表，跟总数比较，找出缺失的序号，发给设备端
            int total = Integer.parseInt(sortId);
            logger.info("-----------收到结束标志,图片总片段数为：" + total);
            List<Integer> sortList = imageDataService.queryByFlag(flag);
            logger.info("====sortList:" + sortList);
            List<Integer> lostList = new ArrayList<>();
            for (int i = 0; i < total; i++) {
                if (!sortList.contains(i)) {
                    lostList.add(i);
                }
            }
            if (lostList.size() > 0) {
                //发送缺失的序号
                logger.info("=====发送缺失的序号：" + lostList);
                DeviceInstructions deviceInstructions = new DeviceInstructions();
                deviceInstructions.setDevId(devId);
                deviceInstructions.setResName(resName);
                deviceInstructions.setContent("endok:" + lostList.toString());
                sendEnd(deviceInstructions);
            } else {
                //发送空请求
                emptyGet(high, low, packet, socket);
                //发送end
                DeviceInstructions deviceInstructions = new DeviceInstructions();
                deviceInstructions.setDevId(devId);
                deviceInstructions.setResName(resName);
                deviceInstructions.setContent("endok:None");
                this.sendEnd(deviceInstructions);
                if (!TransactionServer.observeQueue.isDuplication(flag)) {
                    TransactionServer.observeQueue.add(flag);
                    logger.info("第一次收到图片结束标志，调取保存图片接口flag: " + flag);
                } else {
                    logger.info("重复收到收到图片结束标志，不保存图片flag: " + flag + "不做处理");
                    return;
                }
                //返回 devId / flag / token
                String token = map.get("webtoken") + "";
                logger.info("token:"+token+"----flag:"+flag);
//                RequestBody requestBody = new MultipartBody.Builder()
//                        .setType(MultipartBody.FORM)
//                        .setType( MediaType.parse("Content-type:application/json"))
//                        .addFormDataPart("token", token)
//                        .addFormDataPart("flag", flag)
//                        .build();
//                String response  = HTTPUtil.okHttpPost(requestBody, saveUrl);
                Map<String ,String> dataMap = new HashMap<>();
                dataMap.put("flag",flag);
                dataMap.put("token",token);
                String dataStr = JSON.toJSONString(dataMap);
                try {
                    String response = HTTPUtil.sendPostJson("192.168.60.137",8080,"/iot_aid/photo/show/import", dataStr,15000);
                    logger.info("前端返回："+response);
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
        } else {
            Integer sortId2 = Integer.parseInt(sortId);
            ImageData imgData = new ImageData();
            imgData.setDevId(devId);
            imgData.setValued(value);
            imgData.setResId(resId);
            imgData.setSortId(sortId2);
            imgData.setFlag(flag);
            Timestamp time = new Timestamp(Long.parseLong(inTime));
            logger.info("设备端的发送时间：" + time);
            imgData.setSendTime(time);
            logger.info(devId + " , " + resName + " , " + value + " , " + time);
            imageDataService.addData(imgData);
            logger.info("添加数据成功");
        }
    }

    /**
     * 展示类型
     *
     * @param map
     * @param devId
     * @param resId
     * @param packet
     * @param socket
     */
    @Override
    public void handleType04(Map<String, Object> map, BigInteger devId, Integer resId, DatagramPacket packet, DatagramSocket socket) {
        Integer op=0,tp=1;
        byte[] data = packet.getData();
        String msgID = data[2] + "" + data[3];
        String resName = map.get(nameKey) + "";
        String value = map.get(valueKey) + "";
        String pro = map.get(proKey).toString();
        String modId = map.get(moduleKey).toString();
        Integer proId = Integer.parseInt(pro);

        //设备跟树莓派关联
        Device device = deviceService.queryByProAndMod(proId, modId);
        String hashResult = map.get(hashKey)+"";
        String snCode = raspberryService.querySNByHashResult(hashResult);
        device.setSnCode(snCode);
        deviceService.updateById(device);

        //权限
        if (value.indexOf("op=") != -1 && value.indexOf("tp=") != -1) {
            // 用于权限控制缓冲区
            TransactionServer.getObsMsgID.put(devId + "" + resName, msgID);
            logger.info("收到权限报文回复,放入缓冲区getObsMsgID：" + map);
            Resource resource = new Resource();
            resource.setDevId(devId);
            resource.setResName(resName);
            String[] opTp = value.split(",");
            if (opTp.length > 0 && opTp[0].length() > 3 && opTp[0].startsWith("op=")) {
                String opStr = opTp[0].substring(3, opTp[0].length());
                if (StringUtils.isNumeric(opStr)) {
                    op = Integer.parseInt(opStr);
                }
            }
            if (opTp.length > 1 && opTp[1].length() > 3 && opTp[1].startsWith("tp=")) {
                String opStr = opTp[1].substring(3, opTp[1].length());
                if (StringUtils.isNumeric(opStr)) {
                    tp = Integer.parseInt(opStr);
                }
            }
            resource.setOp(op);
            resource.setTp(tp);
            resourceService.updateResource(resource);
            logger.info("================更新资源的权限：" + resource.toString());
        }
    }

    /**
     * fasong
     * @param deviceInstructions
     */
    public void sendEnd(DeviceInstructions deviceInstructions) {
        List<Short> list = CoapServer.changeMessageID();
        short high = list.get(0);
        short low = list.get(1);
        String res = deviceInstructions.getResName();
        String content = deviceInstructions.getContent();
        byte[] post = {(byte) 0x40, (byte) 0x02, (byte) high, (byte) low};
        byte[] name = res.getBytes();
        byte[] format = {(byte) 0x11, (byte) 0x00};
        byte[] flag = {(byte) 0xff};
        byte[] content1 = content.getBytes();
        byte[] payload = new byte[content1.length + 1];
        System.arraycopy(flag, 0, payload, 0, flag.length);
        System.arraycopy(content1, 0, payload, flag.length, content1.length);
        String getMsg = high + "" + low;
        byte[] resName = new byte[0];
        try {
            resName = MessageUtil.packData(post, name, payload, format);
        } catch (Exception e) {
            e.printStackTrace();
        }
        int count = 0, countNum = 2;
        for (; ; ) {
            //发送
            ResponseData result = deviceInstructService.sendData(deviceInstructions, resName);
            if (result != null) {
                logger.info("设备离线，发送失败");
            }
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
//            if (TransactionServer.postMsgID.containsKey(getMsg)) {
            if(redisUtil.hasKey(getMsg)){
                logger.info("DataType收到回复：" + deviceInstructions);
                return;
            } else {
                if (count > countNum) {
                    logger.info("超过重发次数：" + deviceInstructions);
                    break;
                }
                logger.info("重发：" + deviceInstructions);
                count++;
            }
        }
    }
}
