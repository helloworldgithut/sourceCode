package com.sendi.service;

import com.sendi.entity.DeviceInstructions;
import com.sendi.utils.ResponseData;

/**
 * Created by fengzm on 2019/1/31.
 */
public interface PhotoService {
    /**
     * COAP发送拍照指令到设备
     * @param deviceInstructions
     * @return
     * @throws Exception
     */
    ResponseData takePhoto(DeviceInstructions deviceInstructions) throws  Exception;



}
