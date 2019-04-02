package com.sendi.service;

import com.sendi.entity.DeviceInstructions;
import com.sendi.utils.ResponseData;

/***
    * @Author Mengfeng Qin
    * @Description COAP协议接入的设备指令下发逻辑接口
    * @Date 2019/3/29 11:08
*/
public interface DeviceInstructService {

    /**
     *读取
     * @param deviceInstructions
     * @return
     * @throws Exception
     */
       ResponseData  readResource(DeviceInstructions deviceInstructions) throws  Exception ;

    /**
     * 写入
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
