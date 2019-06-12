package com.sendi.service.impl;

import com.sendi.entity.DeviceInstructions;
import com.sendi.service.PhotoService;
import com.sendi.utils.CoapServer;
import com.sendi.utils.MessageUtil;
import com.sendi.utils.RedisUtil;
import com.sendi.utils.ResponseData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * Created by fengzm on 2019/1/31.
 */
@Service
public class PhotoServiceImpl implements PhotoService {
    private  Logger logger = LoggerFactory.getLogger(getClass());
    private final int countNum = 2;
    @Autowired
    private DeviceInstructServiceImpl deviceInstructService;
    @Autowired
    private RedisUtil redisUtil;
    /**
     * CoAP发送指令给设备拍摄照片
     * @param deviceInstructions
     * @return
     * @throws Exception
     */
    @Override
    public ResponseData takePhoto(DeviceInstructions deviceInstructions) throws Exception {
        int count = 0;
        List<Short> list = CoapServer.changeMessageID();
        short high = list.get(0), low = list.get(1);
        String content = "shoot,"+deviceInstructions.getContent();
        byte []post = {(byte)0x40,(byte)0x02,(byte)high,(byte)low};
        byte []name = deviceInstructions.getResName().getBytes();
        byte format[]={(byte)0x11,(byte)0x00};
        byte []flag  ={(byte) 0xff};
        byte []content1 = content.getBytes();
        byte []payload = new byte[content1.length+1];
        System.arraycopy(flag, 0 ,payload, 0, flag.length);
        System.arraycopy(content1, 0 ,payload, flag.length, content1.length);
        byte[] resName = MessageUtil.packData(post, name, payload, format);
        for(;;){
            ResponseData result = deviceInstructService.sendData(deviceInstructions,resName);
            if (result != null) {
                return result;
            }
            String getMsg = high + "" + low;
            Thread.sleep(4000);
            if(redisUtil.hasKey(getMsg)){
                logger.info(Arrays.toString(resName));
                logger.info("PhotoService收到回复：" + deviceInstructions);
                return ResponseData.success(null);
            }else {
                logger.info("重发：" + deviceInstructions);
                if (count > countNum) {
                    logger.info("超过重发次数："+count+"，重发结束==>" + deviceInstructions);
                    break;
                }
                count++;
            }
        }
        return ResponseData.fail("操作失败，请重新操作");
    }
}
