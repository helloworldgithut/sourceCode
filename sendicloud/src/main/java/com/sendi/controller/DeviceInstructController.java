package com.sendi.controller;

import com.sendi.entity.DeviceInstructions;
import com.sendi.service.DeviceInstructService;
import com.sendi.utils.ResponseData;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
    * @Author Mengfeng Qin
    * @Description COAP协议接入的设备指令下发接口
    * @Date 2019/3/29 10:35
*/
@Api(value = "DeviceInstructController ", tags = {" COAP协议接入的设备指令下发接口"}, description = "读、写、执行")
@RestController
@RequestMapping("/CoAP")
public class DeviceInstructController {
    @Autowired
    private DeviceInstructService deviceInstructService;

    @ApiOperation(value = "读取设备信息", notes = "接口参数信息<br>"
            + "可传参数：<br>"
            + "devId  设备ID  int <br>"
            + "resName 资源名称  String <br>"
            + "handleType 类型  String <br>"
            + "content 内容  String <br>")
    @PostMapping("/readResource")
    public ResponseData readResource(@RequestBody DeviceInstructions deviceInstructions) throws Exception{
        return  deviceInstructService.readResource(deviceInstructions);
    }

    @ApiOperation(value = "向设备写入信息", notes = "接口参数信息<br>"
            + "可传参数：<br>"
            + "devId  设备ID  int <br>"
            + "resName 资源名称  String <br>"
            + "handleType 类型  String <br>"
            + "content 内容  String <br>")
    @PostMapping("/writeResource")
    public ResponseData writeResource(@RequestBody DeviceInstructions deviceInstructions ) throws  Exception{
        return  deviceInstructService.writeResource(deviceInstructions);
    }
    @ApiOperation(value = "下发执行命令给设备信息", notes = "接口参数信息<br>"
            + "可传参数：<br>"
            + "devId  设备ID  int <br>"
            + "resName 资源名称  String <br>"
            + "handleType 类型  String <br>"
            + "content 内容  String <br>")
    @PostMapping("/executeResource")
    public ResponseData excuteResource(@RequestBody DeviceInstructions deviceInstructions ) throws  Exception{
        return  deviceInstructService.excuteResource(deviceInstructions);
    }

}


