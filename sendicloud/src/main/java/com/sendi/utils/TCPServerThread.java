package com.sendi.utils;

import com.alibaba.fastjson.JSON;
import com.sendi.entity.Device;
import com.sendi.entity.Module;
import com.sendi.entity.Product;
import com.sendi.entity.RaspberryUserMerge;
import com.sendi.service.impl.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.Socket;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.sendi.utils.TCPServer.aliveMap;

public class TCPServerThread extends Thread{
    Logger logger = LoggerFactory.getLogger(TCPServerThread.class);
    Socket socket = null;
    Map<String,String> map = new HashMap<>();

    RaspberryService raspberryService = ApplicationContextProvider.getBean(RaspberryService.class);
    DeviceService deviceService = ApplicationContextProvider.getBean(DeviceService.class);
    ModuleServiceImpl moduleService = ApplicationContextProvider.getBean(ModuleServiceImpl.class);
    RaspberryUserMergeService raspberryUserMergeService = ApplicationContextProvider.getBean(RaspberryUserMergeService.class);
    ProductServiceImpl productService = ApplicationContextProvider.getBean(ProductServiceImpl.class);
    public TCPServerThread(){}
    public TCPServerThread(Socket socket){
        this.socket = socket;
    }
    private String snCode;
    private Device deviceA = new Device();

    public void run(){
        try {
            //读取收到的消息
            InputStream ins = socket.getInputStream();
            OutputStream out = socket.getOutputStream();
            byte[] buff =null;
            while(true) {
                buff = new byte[4096];
                int r = ins.read(buff);
                String receive  = new String(buff);
                logger.info(receive);
                if("".equals(receive) || buff[0] ==0){
                    TCPServer.socketMap.remove(map.get("hashResult"));
                    logger.info("empty======remove sockeMap"+deviceA);
                    String modId = deviceA.getModId();
                    Integer proId = deviceA.getProId();
                    Device device = deviceService.queryByProAndMod(proId, modId);
                    deviceService.updateOfflineById(device.getId());
                    raspberryUserMergeService.deleteBySnCode(device.getSnCode());
                    //添加设备下线
                    HTTPUtil.getResponse(device.getId(),new BigInteger("0"));
                    logger.info("RTMP 注销下线"+device.getSnCode());
                }

                Map<String, Object> contentMap = JsonUtil.jsonToMap(receive);
                logger.info("=========转成JSON转成Map对象后"+contentMap.toString());

                String []body = receive.split(",");
                String []data = null;

                    for(int i=0;i<body.length;i++){
                        String body0 = body[i].replace("{","").replace("}","").replace("\"","");
                        data = body0.split(":");
                        map.put(data[0].trim(),data[1].trim());
                    }

                String type = contentMap.get("type").toString();
                String hash = contentMap.get("hash_result").toString();
                String exp_id = contentMap.get("exp_id").toString();
                String modId = contentMap.get("mod_id").toString();
                Integer proId = Integer.parseInt(exp_id);
                Device device = deviceService.queryByProAndMod(proId, modId);
                Product product = productService.queryByProId(proId);
                Integer commit = product.getSubmitStatus();
                Module module = moduleService.queryByMod(modId);
                String devName = module.getName();
                Integer display = module.getDisplayType();

                Map<String ,String> resMap = new HashMap<>();
                //做鉴权管理,如果不是本公司的树莓派，连接断开
                if(raspberryService.queryByHashResult(hash)>0){
                    //注册消息，把socket put 进socketMap
                    TCPServer.socketMap.put(hash,socket);
                    snCode = raspberryService.querySNByHashResult(hash);
                    //根据产品ID查出协议，判断协议是否是RTMP
                    String protocol = product.getProtocol();
                    if(!"RTMP".equals(protocol)){
                        resMap.put("type","RTMP");
                        resMap.put("hashResult",snCode);
                        resMap.put("content","reg_err");
                        out.write( JsonUtil.toJsonByte(resMap));
                        logger.info("创建的产品不是RTMP协议，视频传输用RTMP协议");
                        return ;
                    }
//====================type==============================
                    if("50".equals(type)){
                        //首先判断产品是否已经创建
                        int n = productService.productList(proId);
                        if(n<=0){
                            resMap.put("type","10");
                            resMap.put("hashResult",snCode);
                            resMap.put("content","reg_err");
                            out.write( JsonUtil.toJsonByte(resMap));
                            logger.info("产品未创建，请先创建产品！");
                            long uptime = System.currentTimeMillis();

                            return;
                        }

                        //====================add or update device===================
                        if(device == null && product.getProType().equals("人工智能")){
                            if(commit == 1){
                                logger.info("该产品已经提交过了");
                                return;
                            }
                            device = new Device();
                            device.setProId(proId);
                            device.setModId(modId);
                            device.setDataSecret(1);
                            device.setDevName(devName);
                            device.setSnCode(snCode);
                            device.setDisplayType(4);
                            device.setCreateTime(new Date());
                            logger.info("添加设备的sn码："+snCode);
                            deviceService.addDevice(device);
                            logger.info("人工智能 添加设备成功： "+device);
                        }else if(device == null && !product.getProType().equals("人工智能")){
                            if(commit == 1){
                                logger.info("该产品已经提交过了");
                                return;
                            }
                            device = new Device();
                            device.setProId(proId);
                            device.setModId(modId);
                            device.setDataSecret(1);
                            device.setDevName(devName);
                            device.setSnCode(snCode);
                            device.setDisplayType(display);
                            device.setCreateTime(new Date());
                            if(modId.equals("10020030040001") ||  modId.equals("10020030040002")){
                                device.setAttribute(0);
                            }else {
                                device.setAttribute(4);
                            }
                            logger.info("添加设备的sn码："+snCode);
                            deviceService.addDevice(device);
                            logger.info("RTMP 添加设备成功： "+device);
                        }else {
                            if(product.getProType().equals("人工智能")){
                                if(commit == 1){
                                    logger.info("该产品已经提交过了");
                                    return;
                                }
                                device.setDataSecret(1);
                                device.setDisplayType(4);
                                device.setSnCode(snCode);
                                deviceService.updateById(device);
                                logger.info("update  device:"+device);
                            }else if(!product.getProType().equals("人工智能")){
                                if(commit >= 1){
                                    logger.info("RTMP-该产品已经提交过了");
                                    return;
                                }
                                device.setDataSecret(1);
                                device.setDisplayType(display);
                                device.setSnCode(snCode);
                                deviceService.updateById(device);
                                logger.info("update  device:"+device);
                            }else {
                                resMap.put("type","10");
                                resMap.put("hashResult",snCode);
                                resMap.put("content","reg_err");
                                out.write( JsonUtil.toJsonByte(resMap));
                                logger.info("产品领域有误"+product.getProType());
                            }

                        }
//====================add or update device end =============================
                        try {
                            //添加树莓派跟用户的关联
                            int num = raspberryUserMergeService.queryNumBySnCode(snCode);
                            RaspberryUserMerge raspberryUserMerge = new RaspberryUserMerge();
                            String userId = product.getUserId();
                            if(num <= 0){
                                raspberryUserMerge.setUserId(userId);
                                raspberryUserMerge.setSnCode(snCode);
                                raspberryUserMergeService.addRaspberryUserMerge(raspberryUserMerge);
                            }else {
                                raspberryUserMerge = raspberryUserMergeService.queryBySnCode(snCode);
                                String user = raspberryUserMerge.getUserId();
                                if(!userId.equals(user)){
                                    resMap.put("type","10");
                                    resMap.put("hashResult",snCode);
                                    resMap.put("content","reg_err");
                                    out.write( JsonUtil.toJsonByte(resMap));
                                    logger.info("树莓派已经被注册,不能两个用户同时登录一块树莓派！！！");
                                    logger.info("用户1ID=="+userId);
                                    logger.info("用户2ID=="+user+"正在使用");
                                    return;
                                }
                            }
                            resMap.put("type","09");
                            resMap.put("hashResult",hash);
                            resMap.put("content","reg_ok");
                            out.write( JsonUtil.toJsonByte(resMap));

                            BigInteger devId = deviceService.queryByProAndMod(proId,modId).getId();
                            deviceService.updateOnlineById(devId);
                            logger.info("SN码为 "+snCode+" 的设备注册成功！"+device);

                            aliveMap.put(device.getId(),System.currentTimeMillis());
                            deviceA = device;
                        }catch (Exception e){
                            resMap.put("type","404");
                            resMap.put("hashResult",hash);
                            resMap.put("content","reg_err");
                            out.write( JsonUtil.toJsonByte(resMap));
                            logger.info("产品不存在，连接断开！");
                            socket.close();
                        }


        ////////////////////////////////////////////
                    }else if("51".equals(type)){
                        //首先判断产品是否已经创建
                        int n = productService.productList(proId);
                        if(n<=0){
                            resMap.put("type","10");
                            resMap.put("hashResult",snCode);
                            resMap.put("content","reg_err");
                            out.write( JsonUtil.toJsonByte(resMap));
                            logger.info("产品未创建，请先创建产品！");
                            long uptime = System.currentTimeMillis();
                            return;
                        }
                        logger.info("收到心跳包");
                        long uptime = System.currentTimeMillis();
                        aliveMap.put(device.getId(),uptime);
//                        break;
                    }else {
                        resMap.put("type","TypeError");
                        resMap.put("hashResult",hash);
                        resMap.put("content","reg_err");
                        out.write( JsonUtil.toJsonByte(resMap));
                        logger.info(receive);
                        return;
                    }
//====================type  end==============================

                }else {
                    resMap.put("type","404");
                    resMap.put("hashResult",hash);
                    resMap.put("content","reg_err");
                    out.write( JsonUtil.toJsonByte(resMap));
                    logger.info("RTMP 不是本公司的树莓派，连接断开！");
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    socket.close();
                }
            }//while end
        } catch (IOException e) {
            logger.info("TCP 线程异常"+e.getMessage());
            logger.info("客户端连接断开！"+"移除"+map.get("hashResult"));
            TCPServer.socketMap.remove(map.get("hashResult"));
            String modId = map.get("mod_id");
            String expId = map.get("exp_id");
            Integer proId = Integer.parseInt(expId);
            Device device = deviceService.queryByProAndMod(proId, modId);
            deviceService.updateOfflineById(device.getId());
            logger.info("设备下线"+device);
            //解除绑定树莓派与用户的关联
            Product product = productService.queryByProId(proId);
            raspberryUserMergeService.deleteBySnCode(snCode);
            /*
            Map<String ,String> dataMap = new HashMap<>();
            dataMap.put("deviceId ",device.getId().toString());
            dataMap.put("status","0");
            String dataStr = JSON.toJSONString(dataMap);
            try {
//                String response = HTTPUtil.sendPostJson("127.0.0.1",8080,"/iot_aid/device/status", dataStr,15000);
                String response = HTTPUtil.sendPostJson("192.168.60.137",8080,"/iot_aid/device/status", dataStr,15000);
                logger.info("前端返回："+response);
            } catch (IOException m) {
                m.printStackTrace();
            }
            */
            logger.info("解除用户跟树莓派的关联，TCPServer.socketMap的大小为："+TCPServer.socketMap.size());
            for(String key : TCPServer.socketMap.keySet()){
                System.out.println(key+":"+TCPServer.socketMap.get(key));
            }
        }
    }

}
