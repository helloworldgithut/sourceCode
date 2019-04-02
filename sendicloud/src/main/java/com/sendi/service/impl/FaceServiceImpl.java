package com.sendi.service.impl;

import com.alibaba.fastjson.JSON;
import com.sendi.config.Msg;
import com.sendi.dao.RaspberryDaoI;
import com.sendi.entity.Device;
import com.sendi.entity.ImageData;
import com.sendi.entity.receiveImgBody;
import com.sendi.service.FaceService;
import com.sendi.utils.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 人脸识业务别逻辑类
 * Created by fengzm on 2019/1/30.
 */
@Service
public class FaceServiceImpl implements FaceService {

    private Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private RaspberryDaoI proConfigDao;
    @Autowired
    private RaspberryDaoI raspberryDao;
    @Autowired
    private DeviceService deviceService;
    @Autowired
    private RaspberryUserMergeService raspberryUserMergeService;
    @Autowired
    private ImageDataService imageDataService;

    /**
     * 发送图片给设备端
     * @param snCode
     * @param file
     * @return
     * @throws Exception
     */
    @Override
    public ResponseData sendImg(String snCode, MultipartFile file) throws Exception {
        logger.info("***********" + file.getOriginalFilename());
        String hashResult = proConfigDao.queryHashResultBySnCode(snCode);
        InputStream ins = file.getInputStream();
        byte[] byt = new byte[ins.available()];
        ins.read(byt);
        String content = Byte2Base64.byte2Base64StringFun(byt);
        OutputStream outs = null;
        Map<String, Object> map = new HashMap<String, Object>(3);
        Socket socket = TCPServer.socketMap.get(hashResult);
        try {
            outs = socket.getOutputStream();
            map.put(Msg.TYPE_KEY,Msg.TYPE_05);
            map.put(Msg.HASHRESULT_KEY, hashResult);
            map.put(Msg.CONTENT_KEY, content);
            outs.write(JsonUtil.toJsonByte(map));
            outs.flush();
        } catch (IOException e) {
            logger.info(e.getMessage()+"IOException");
            return offLine(snCode,hashResult);
//            return ResponseData.fail("设备端未连接！发送失败！");
        } catch (NullPointerException e) {
            logger.info(e.getMessage()+"NullPointerException");
            return offLine(snCode,hashResult);
//            return ResponseData.fail("设备端未连接！发送失败！");
        }
        return ResponseData.success(null);
    }

    @Override
    public ResponseData stop(String snCode, String type, String content) throws Exception {
        OutputStream outs = null;
        Socket socket = null;
        String hashResult = proConfigDao.queryHashResultBySnCode(snCode);
        Map<String, Object> map = new HashMap<String, Object>(3);
//        if(Msg.START.equals(type)){
//            map.put(Msg.TYPE_KEY, Msg.TYPE_03);
//        }else if(Msg.STOP.equals(type)){
            map.put(Msg.TYPE_KEY, Msg.TYPE_04);
//        } else {
//            return ResponseData.fail("操作失败");
//        }
        map.put(Msg.HASHRESULT_KEY, hashResult);
        map.put(Msg.CONTENT_KEY, content);
        try {

            socket = TCPServer.socketMap.get(hashResult);
            outs = socket.getOutputStream();
            outs.write(JsonUtil.toJsonByte(map));
            outs.flush();
        } catch (IOException e) {
            if (outs != null) {
                outs.close();
            }
            if (socket != null) {
                socket.close();
            }
            logger.info("stop======"+e.getMessage());
            return offLine(snCode,hashResult);
//            return ResponseData.fail("设备端未连接！发送失败！");
        } catch (NullPointerException e) {
            logger.info("stop======"+e.getMessage());
            return offLine(snCode,hashResult);
//            return ResponseData.fail("设备端未连接！发送失败！");
        }
        return ResponseData.success(null);
    }

    /**
     * 开始/停止 比对 （图像与视频流比对）
     *
     * @param snCode
     * @param content
     * @return
     * @throws Exception
     */
    @Override
    public ResponseData start(String snCode, String content, MultipartFile file) throws Exception {
        OutputStream outs = null;
        Socket socket = null;
        String hashResult = proConfigDao.queryHashResultBySnCode(snCode);
        logger.info("file==" + file.getOriginalFilename());
        InputStream ins = file.getInputStream();
        byte[] byt = new byte[ins.available()];
        logger.info("=====size:"+ins.available());
        ins.read(byt);
        String imgContent = Byte2Base64.byte2Base64StringFun(byt);
//        logger.info(imgContent);
        Map<String, Object> map = new HashMap<String, Object>(3);
        map.put(Msg.TYPE_KEY, Msg.TYPE_03);
        map.put(Msg.HASHRESULT_KEY, hashResult);
        map.put(Msg.CONTENT_KEY, imgContent);
        map.put("url",content);
        long start =System.currentTimeMillis();

        try {
            socket = TCPServer.socketMap.get(hashResult);
            outs = socket.getOutputStream();
            logger.info("***************发送前");
            outs.write(JsonUtil.toJsonByte(map));
//            outs.flush();
//            outs.close();
            logger.info("**************发送后");
        } catch (Exception e) {
            if (outs != null) {
                outs.close();
            }
            if (socket != null) {
                socket.close();
            }
            logger.info("start======IOException:"+e.getMessage());
            return offLine(snCode,hashResult);
        }
//        catch (NullPointerException e) {
//            logger.info("start======NullPointerException:"+e.getMessage());
//            return offLine(snCode,hashResult);
//        }
        finally {
            logger.info("发送到设备端需要的时间"+(System.currentTimeMillis()-start)+"ms");
        }
        return ResponseData.success(null);
    }

    /**
     * 拉流接口，拼接一个拉流地址给前端
     * @param snCode
     * @param content
     * @return
     * @throws Exception
     */
    @Override
    public ResponseData pushStream(String snCode, String content) throws Exception {
        OutputStream outs = null;
        Socket socket = null;
        String hashResult = proConfigDao.queryHashResultBySnCode(snCode);
        Map<String, Object> map = new HashMap<String, Object>(3);
        if(Msg.PUSH.equals(content)){
            map.put(Msg.TYPE_KEY, Msg.TYPE_03);
        }else if (Msg.PULL.equals(content)){
            map.put(Msg.TYPE_KEY, Msg.TYPE_04);
        }else {
            return ResponseData.fail("操作失败");
        }
        map.put(Msg.HASHRESULT_KEY, hashResult);
        map.put(Msg.CONTENT_KEY, content);
        try {
            socket = TCPServer.socketMap.get(hashResult);
            outs = socket.getOutputStream();
            outs.write(JsonUtil.toJsonByte(map));
        } catch (IOException e) {
            if (outs != null) {
                outs.close();
            }
            if (socket != null) {
                socket.close();
            }
            logger.info("---------IOExcepion:"+e.getMessage());
            return offLine(snCode, hashResult);
        } catch (NullPointerException e) {
            logger.info("socket is null"+e.getMessage());
            return offLine(snCode, hashResult);
        }
        return ResponseData.success(null);
    }

    @Override
    public ResponseData takePhoto(String snCode, String content) {
        OutputStream outs = null;
        Socket socket = null;
        String hashResult = proConfigDao.queryHashResultBySnCode(snCode);
        Map<String, Object> map = new HashMap<String, Object>(3);
        map.put(Msg.TYPE_KEY, Msg.TYPE_00);
        map.put(Msg.HASHRESULT_KEY, hashResult);
        map.put(Msg.CONTENT_KEY, content);
        try {
            socket = TCPServer.socketMap.get(hashResult);
            outs = socket.getOutputStream();
            outs.write(JsonUtil.toJsonByte(map));
        } catch (IOException e) {
            logger.info("takePhoto======"+e.getMessage());
            return offLine(snCode,hashResult);
        } catch (NullPointerException e) {
            logger.info("takePhoto======"+e.getMessage());
            return offLine(snCode,hashResult);
        }
        return ResponseData.success(null);
    }

    @Override
    public void receiveImage(receiveImgBody revBody) {
        if(revBody != null){
            if ("30".equals(revBody.getType())) {
                Device dev = deviceService.queryByProAndMod(revBody.getExp_id(), revBody.getMod_id());
                Integer sortId = revBody.getSort_id();
                String valued = revBody.getValue();
                Long inTime = revBody.getTime();
                String flag = revBody.getFlag();
                String token = revBody.getWebtoken();
                logger.info(valued.length()+"");
                //按1024个字符串来给图片的Base64编码分段
                List<String> list =  getStrList(valued,1024);
                for(int i=0;i<list.size();i++) {
//                    logger.info("图片片段"+list.get(i));
                    ImageData imageData = new ImageData();
                    imageData.setDevId(dev.getId());
                    imageData.setSortId(i);
                    imageData.setValued(list.get(i));
                    imageData.setFlag(flag);
                    Timestamp time = new Timestamp(inTime);
                    imageData.setSendTime(time);
                    imageDataService.addData(imageData);
                    logger.info("添加imageData数据成功");
                }
        //************响应到前端*****************
                Map<String, String> dataMap = new HashMap<>();
                dataMap.put("flag", flag);
                dataMap.put("token", token);
                String dataStr = JSON.toJSONString(dataMap);
                try {
                    String response = HTTPUtil.sendPostJson("192.168.60.137", 8080, "/iot_aid/photo/show/import", dataStr, 15000);
                    logger.info("前端返回：" + response);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else {
                logger.info("******data receive: data type error ******");
            }
        }else {
            logger.info("****** data receive: body empty error ****** ");
        }
    }

    /**
     * 设备下线，并解除用户与树莓派的关联
     * @param snCode
     * @param hashResult
     * @return
     */
    public ResponseData offLine(String snCode, String hashResult){
        deviceService.updateOfflineBySnCode(snCode);
        raspberryUserMergeService.deleteBySnCode(snCode);
        TCPServer.socketMap.remove(hashResult);
        logger.info("FaceServiceImpl"+ResponseData.fail("发送失败，设备已离线"));
        return ResponseData.fail("发送失败，设备已离线");
    }

    /**
     *  以下三个方法是用来按长度截取图片的Base64编码
     * @param inputString
     * @param length
     * @return
     */
    public  List<String> getStrList(String inputString, int length) {
        int size = inputString.length() / length;
        if (inputString.length() % length != 0) {
            size += 1;
        }
        return getStrList(inputString, length, size);
    }

    public  List<String> getStrList(String inputString, int length, int size) {
        List<String> list = new ArrayList<String>();
        for (int index = 0; index < size; index++) {
            String childStr = substring(inputString, index * length, (index + 1) * length);
            list.add(childStr);
        }
        return list;
    }

    public  String substring(String str, int f, int t) {
        if (f > str.length()){
            return null;
        }
        if (t > str.length()) {
            return str.substring(f, str.length());
        } else {
            return str.substring(f, t);
        }
    }

}

