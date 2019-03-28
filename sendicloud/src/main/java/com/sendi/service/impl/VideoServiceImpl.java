package com.sendi.service.impl;

import com.sendi.config.Msg;
import com.sendi.dao.RaspberryDaoI;
import com.sendi.entity.ReqBody;
import com.sendi.service.VideoService;
import com.sendi.utils.JsonUtil;
import com.sendi.utils.ResponseData;
import com.sendi.utils.TCPServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

/***
    * @Author MengfengQin
    * @Description
    * @Date 2019/3/26 17:48
*/
@Service
public class VideoServiceImpl implements VideoService {
    private Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
     private RaspberryDaoI raspberryDao;
    @Autowired
    private DeviceService deviceService;
    @Autowired
    private RaspberryUserMergeService raspberryUserMergeService;
    /**
     * 实时视频
     * @param reqBody
     * @return
     * @throws Exception
     */
    @Override
    public ResponseData push(ReqBody reqBody) throws Exception {
        Map<String, String> resMap = new HashMap<>(3);
        logger.info("前端发来的请求："+reqBody);
        String snCode = reqBody.getSnCode();
        String url = reqBody.getContent();//拉流地址
        String hashResult = raspberryDao.queryHashResultBySnCode(snCode);
        Socket socket;
        try {
            socket =  TCPServer.socketMap.get(hashResult);
            OutputStream out = socket.getOutputStream();
            resMap.put(Msg.TYPE_KEY,Msg.TYPE_08);
            resMap.put(Msg.HASHRESULT_KEY,hashResult);
            //拼接一个拉流地址发给前端设备端
            resMap.put(Msg.CONTENT_KEY,url);
            out.write(JsonUtil.toJsonByte(resMap));
        }catch (IOException e){
            logger.info("---------IOExcepion");
            //todo 设备离线、解除用户与树莓派的绑定
            deviceService.updateOfflineBySnCode(snCode);
            raspberryUserMergeService.deleteBySnCode(snCode);
            logger.info(e.getMessage());
            TCPServer.socketMap.remove(hashResult);
            return ResponseData.fail("---发送失败");
        }catch (NullPointerException e){
            logger.info("socket is null");
            //todo 设备离线、解除用户与树莓派的绑定
            deviceService.updateOfflineBySnCode(snCode);
            raspberryUserMergeService.deleteBySnCode(snCode);
            TCPServer.socketMap.remove(hashResult);
            return ResponseData.fail("发送失败，设备已离线");
        }
        logger.info("===============已发送");
        return ResponseData.success(null);
    }
}
