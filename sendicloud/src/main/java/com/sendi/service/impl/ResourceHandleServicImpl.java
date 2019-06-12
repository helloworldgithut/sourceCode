package com.sendi.service.impl;

import com.alibaba.fastjson.JSON;
import com.sendi.entity.Device;
import com.sendi.entity.Module;
import com.sendi.entity.Resource;
import com.sendi.service.UDPHandleService;
import com.sendi.utils.CoapServer;
import com.sendi.utils.HTTPUtil;
import com.sendi.utils.RedisUtil;
import com.sendi.utils.TransactionServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigInteger;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.*;

/**
 * 收到资源报文处理类
 * Created by fengzm on 2019/2/1.
 */
@Service
public class ResourceHandleServicImpl extends UDPHandleService {
    @Autowired
    private ResourceService resourceService;
    @Autowired
    private DeviceService deviceService;
    @Autowired
    private ModuleServiceImpl moduleService;
    @Autowired
    private RedisUtil redisUtil;
    /**
     * 收到的资源 0x60 0x45 [] [] 0xc1 0x28
     *
     * @param content
     * @param packet
     * @param socket
     */
    @Override
    public void doHandle(String content, DatagramPacket packet, DatagramSocket socket) {
        byte[] data = packet.getData();
        String msgID = data[2] + "" + data[3];
        logger.info("收到Discover的msgID====="+msgID+" ,get"+redisUtil.get(msgID));
//        if (!TransactionServer.ResponID.containsKey(msgID)) {
        if(!redisUtil.hasKey(msgID)){
            logger.info(" Discover 发送失败,msgID:" + msgID);
            return;
        }
//        String discoverKey = TransactionServer.ResponID.get(msgID);
        String discoverKey = redisUtil.get(msgID).toString();
        //删除
//        TransactionServer.ResponID.remove(msgID);
        redisUtil.del(msgID);
//        TransactionServer.discoverID.put(discoverKey, msgID);
        redisUtil.set(discoverKey,msgID,30);
//        TransactionServer.discoverID.put(discoverKey, msgID);

        System.out.println(discoverKey+"========="+msgID);
        if (!TransactionServer.discoverQueue.isDuplication(msgID)) {
            TransactionServer.discoverQueue.add(msgID);
            logger.info("第一次收到Discover 的msgID:" + msgID);
        } else {
            logger.info("收到重复Discover 的msgID:" + msgID + "不做处理");
            return;
        }
        List<String> resList = new ArrayList<>();
        int begin, last;
        Resource resource = null;
        String rs = "</";
        int indexx = content.indexOf(rs);
        String datacontent = content.substring(indexx);
        logger.info("收到的资源列表：" + datacontent);
        String[] res1 = datacontent.split(",");
        for (int i = 0; i < res1.length; i++) {
            begin = res1[i].indexOf("</") + 2;
            last = res1[i].indexOf(">");
                String res = res1[i].substring(begin, last);
                if (res != null && !res.equals("")) {
                    resList.add(res);
            }
        }
        //截取发现的设备,判断模块是否是本公司的，判断设备是否存在，更新设备状态
        String str = "/";
        for (String s : resList) {
            String[] devData = s.split(str);
            Integer proId = Integer.parseInt(devData[0]);
            String modId = devData[1];
            String resName = devData[2];
            int n = moduleService.queryByModId(modId);
            if(n<0){
                logger.info("该模块不是本公司的模块，请使用本公司的模块");
                return;
            }
            Device device = deviceService.queryByProAndMod(proId, modId);
            logger.info("queryByProAndMod device:"+device);
            if(device==null){
                //设备不存在，先添加设备，更新设备状态为在线
                //根据modId 查module 得出Name、displayType
                device = new Device();
                Module module = moduleService.queryByMod(modId);
                device.setProId(proId);
                device.setModId(modId);
                device.setIp(packet.getAddress().toString().replace("/",""));
                device.setPort(packet.getPort());
                device.setDataSecret(1);
                device.setDevName(module.getName());
                device.setCreateTime(new Date());
                device.setDisplayType(module.getDisplayType());
                logger.info("====== add displayType======="+module.getDisplayType());
//                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
//                String createTime= df.format(new Date());// new Date()为获取当前系统时间
                if(modId.equals("10020030040001") ||  modId.equals("10020030040002")){
                    device.setAttribute(0);
                }else {
                    device.setAttribute(3);
                }
                //1图片2视频3资源，根据modID 查询，再set
                deviceService.addDevice(device);
                device = deviceService.queryByProAndMod(device.getProId(),device.getModId());
                logger.info("=============ADD Device"+device.getId());
                deviceService.updateOnlineById(device.getId());
                //添加上线通知
                HTTPUtil.getResponse(device.getId(),new BigInteger("1"));

            }else{
                //设备已经存在，更新设备信息，设备状态为在线
                Module module = moduleService.queryByMod(modId);
                BigInteger id = device.getId();
                device.setIp(packet.getAddress().toString().replace("/",""));
                device.setPort(packet.getPort());
                device.setDevName(module.getName());
                //displayType
                logger.info("========="+module);
                logger.info("====== update displayType======="+module.getDisplayType());
                device.setDisplayType(module.getDisplayType());
                //更新设备的devName 、IP、PORT、displayType
                deviceService.updateById(device);
                //更新state、在线/离线
                deviceService.updateOnlineById(id);
                 //添加上线通知
                HTTPUtil.getResponse(device.getId(),new BigInteger("1"));

            }

            BigInteger devId = device.getId();
            //把设备下的资源state 都先置0（上次发现的）
            resourceService.updateOfflineByDevId(devId);
            Long uptime = System.currentTimeMillis();
            logger.info("=======ResourceHandleServiceImpl lifeMap key ====="+devId);
            CoapServer.lifeMap.put(devId, uptime);
        }

        for (String s : resList) {
            String[] devData = s.split(str);
            Integer proId = Integer.parseInt(devData[0]);
            String modId = devData[1];
            String resName = devData[2];
            Device device = deviceService.queryByProAndMod(proId, modId);
             BigInteger devId = device.getId();
            logger.info("===============ResourceHandleServicImpl"+devId);
            //更新数据库资源状态
            resource = new Resource();
            resource.setDevId(devId);
            resource.setResName(s);
            resource.setDisplayName(resName);
            logger.info("=========updateOrAddResource======"+devId);
            resourceService.updateOrAddResource(resource);
        }

        for (String s : resList) {

            String[] devData = s.split(str);
            Integer proId = Integer.parseInt(devData[0]);
            String modId = devData[1];
            String resName = devData[2];
            Device device = deviceService.queryByProAndMod(proId, modId);
            BigInteger devId = device.getId();
            //一次发完所有资源的Get
            //            if(){
//
//            }
            //发送get
            sendResourceGet(s, packet, devId, socket);
            //发送observe
            sendResourceObserve(s, packet, devId, socket);
        }
        //更新数据库资源状态
//        for (String s : resList) {
//            resource = new Resource();
//            resource.setDevId(devId);
//            resource.setResName(s);
//            resourceService.updateOrAddResource(resource);
//        }
//        for (String res : resList) {
//            //发送get
//            sendResourceGet(res, packet, devId, socket);
//            //发送observe
//            sendResourceObserve(res, packet, devId, socket);
//            Long uptime = System.currentTimeMillis();
//            CoapServer.lifeMap.put(devId, uptime);
//        }
    }

    /**
     * 发送资源get 请求,用于设备上传资源权限的
     *
     * @param res
     * @param packet
     * @param devId
     */
    public void sendResourceGet(String res, DatagramPacket packet, BigInteger devId, DatagramSocket socket) {
        List<Short> list = CoapServer.changeMessageID();
        Integer op = 0, tp = 1;
        short highGet = list.get(0);
        short lowGet = list.get(1);
        String str = highGet + "" + lowGet;
        logger.info("资源发送Get请求mesId" + str);
        int count = 0;
        for (; ; ) {
            sendGet(res, "op,tp", highGet, lowGet, packet, socket);
//            for(int i=0;i<3;i++){
//
//            }
            try {
                Thread.sleep(4000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            String mm = TransactionServer.revGetObsMsgID.get(devId + "" + res);
            logger.info("获取缓冲区的key:" + devId + "" + res);
            logger.info("++++++本地的 getObsMsgID key++++++++" + devId + "" + res + "==msgIdDtr=" + str);
            logger.info("++++++缓冲区 getObsMsgID key++++++++" + devId + "" + res + "==msgIdDtr=" + mm);
            if (str.equals(mm)) {
//                //加空请求
//                emptyGet(highGet, lowGet, packet, socket);
                break;
            } else {
                logger.info("Get重发的MsgId：" + highGet + "" + lowGet);
                sendGet(res, "op,tp", highGet, lowGet, packet, socket);
                if (count > 3) {
                    Resource resource = new Resource();
                    resource.setDevId(devId);
                    resource.setResName(res);
                    resource.setOp(op);
                    resource.setTp(tp);
                    resourceService.updateResource(resource);
                    logger.info("================发送Get请求失败，更新资源的权限：" + resource.toString());
                    break;
                }
                count++;
                logger.info("Get重发次数：" + count);
            }
        }
    }



    /**
     *发送资源Observe 逻辑
     * @param res
     * @param packet
     * @param devId
     */
    protected void sendResourceObserve(String res, DatagramPacket packet, BigInteger devId, DatagramSocket socket){
        List<Short> list =  CoapServer.changeMessageID();
        short high = list.get(0);
        short low= list.get(1);
        String str = high+""+low;
        logger.info("=======str======"+str);
        int count = 0;
        for (;;){

            observeRes(res,high,low,packet,socket);
            try {
                Thread.sleep(4000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
//            String kk = TransactionServer.obsMsgID.get(devId+""+res);
            boolean bl = redisUtil.hasKey(devId+""+res);
            String kk = "";
            if(bl){
                kk = redisUtil.get(devId+""+res).toString();
            }
            logger.info("==============="+devId+""+res);
            logger.info("++++++Observe msgID key++++++++"+devId+""+res+"==str="+str);
            logger.info("++++++Observe msgID key++++++++"+devId+""+res+"==RR="+kk);
            if(str.equals(kk)){
//                deviceService.updateOnlineById(devId);
//                //加空请求
//                emptyGet(high, low, packet, socket);

                break;
            }else  {
                logger.info("Observe重发：" + str);
                observeRes(res,high,low,packet,socket);
                if(count > 3) {
                    break;
                }
                count ++;
                logger.info("Observe重发次数："+count);
            }


        }
    }

    /**
     * 批量发送 GET Obserer 请求
     * @param resList 资源列表
     * @param str 分隔符
     * @param packet  数据包
     * @param socket
     */

    public void batchSend(ArrayList<String>  resList,String str, DatagramPacket packet, DatagramSocket socket){
        // 先初始化好msgID
        Map<String, String> getMsgIdList = new HashMap<>();
        Map<String, String> observeMsgIdList = new HashMap<>();
        for (String s : resList) {
            List<Short> list = CoapServer.changeMessageID();
            getMsgIdList.put(s, list.get(0) + str+ list.get(1));
            list = CoapServer.changeMessageID();
            observeMsgIdList.put(s, list.get(0) + str + list.get(1));
        }
        int repeatSend = 0;
        while (true) {
            if (resList.size() == 0) {
                logger.info("=============权限GET、bserve 全部收到回复");
                break;
            }
            if (repeatSend >= 4) {
                if (getMsgIdList.size() > 0) {
                    logger.info("=============权限GET 存在没有收到回复，资源：" + getMsgIdList.keySet());
                    Integer op = 0, tp = 1;
                    for (String name : getMsgIdList.keySet()) {
                        String[] devData = name.split(str);
                        Integer proId = Integer.parseInt(devData[0]);
                        String modId = devData[1];
                        Device device = deviceService.queryByProAndMod(proId, modId);
                        BigInteger devId = device.getId();
                        Resource updataResource = new Resource();
                        updataResource.setDevId(devId);
                        updataResource.setResName(name);
                        updataResource.setOp(op);
                        updataResource.setTp(tp);
                        resourceService.updateResource(updataResource);
                        logger.info("================发送Get请求失败，更新资源的权限：" + updataResource.toString());
                    }
                }
                if (observeMsgIdList.size() > 0) {
                    logger.info("=============observe 存在没有收到回复，资源：" + observeMsgIdList.keySet());
                }
                break;

            }

            for (String s : resList) {
                String[] devData = s.split(str);
                Integer proId = Integer.parseInt(devData[0]);
                String modId = devData[1];
                Device device = deviceService.queryByProAndMod(proId, modId);
                BigInteger devId = device.getId();
                String[] getMsgId = getMsgIdList.get(s).split(str);
                Short getHigh = Short.valueOf(getMsgId[0]);
                Short getLow = Short.valueOf(getMsgId[1]);
                String getStr = getHigh + "" + getLow;
                String repGetStr = TransactionServer.revGetObsMsgID.get(devId + "" + getStr);
                int count = 0;
                if (getStr.equals(repGetStr)) {
                    count++;
                    logger.info("权限GET收到回复，资源：" + s);
                    getMsgIdList.remove(s);
                } else {
                    //发送get
                    logger.info("==========GET请求发送");
                    sendGet(s, "op,tp", getHigh, getLow, packet, socket);
                }
                String[] observeMsg = observeMsgIdList.get(s).split(str);
                Short observeHigh = Short.valueOf(observeMsg[0]);
                Short observeLow = Short.valueOf(observeMsg[1]);
                String obsStr = observeHigh + "" + observeLow;
                String repObsStr = TransactionServer.revGetObsMsgID.get(devId + "" + obsStr);
                if (obsStr.equals(repObsStr)) {
                    count++;
                    logger.info("observe收到回复，资源：" + s);
                    getMsgIdList.remove(getMsgIdList.get(s));
                } else {
                    //发送Observe
                    logger.info("==========GET请求发送");
                    observeRes(s, observeHigh, observeLow, packet, socket);
                }
                if (count == 2) {
                    //移除资源
                    resList.remove(s);
                }
            }
            logger.info("==========repeatSend 重发次数：" + repeatSend);
            repeatSend++;
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }

    }



}
