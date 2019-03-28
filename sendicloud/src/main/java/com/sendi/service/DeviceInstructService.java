package com.sendi.service;

import com.sendi.entity.DeviceInstructions;
import com.sendi.utils.ResponseData;

/**
 *COAP协议接入的设备指令下发逻辑接口
 * Created by fengzm on 2019/1/28.
 */
public interface DeviceInstructService {

    /**
     *读
     * @param deviceInstructions
     * @return
     * @throws Exception
     */
       ResponseData  readResource(DeviceInstructions deviceInstructions) throws  Exception ;

    /**
     *
     * 写
     * @param deviceInstructions
     * @return
     * @throws Exception
     */
    ResponseData  writeResource(DeviceInstructions deviceInstructions) throws  Exception ;

    /**
     *执行
     * @param deviceInstructions
     * @return
     * @throws Exception
     */

    ResponseData  excuteResource(DeviceInstructions deviceInstructions) throws  Exception ;



}
