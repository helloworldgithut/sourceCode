package com.sendi.controller;

import com.sendi.entity.DeviceInstructions;
import com.sendi.service.PhotoService;
import com.sendi.service.impl.ImageDataService;
import com.sendi.utils.ResponseData;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
/**
    * @Author Mengfeng Qin
    * @Description 设备拍摄照片接口
    * @Date 2019/3/29 10:47
*/
@Api(value = "PhotoController ", tags = {" 设备拍摄照片接口"}, description = "下发指令给设备拍照")
@Controller
@RequestMapping("/photo")
public class PhotoController {
    @Autowired
    private PhotoService photoService;
    @Autowired
    private ImageDataService imageDataService;

    @ApiOperation(value = "设备拍摄照片接口", notes = "接口参数信息<br>"
            + "可传参数：<br>"
            + "devId  设备ID  int <br>"
            + "resName 资源名称  String <br>"
            + "handleType 类型  String <br>"
            + "content 内容  String <br>")
    @PostMapping("/takePhoto")
    @ResponseBody
    public ResponseData takePhoto(@RequestBody DeviceInstructions deviceInstructions) throws Exception{
        return  photoService.takePhoto(deviceInstructions);
    }

    @ApiOperation(value = "测试照片接口，生成图片在桌面", notes = "接口参数信息<br>"
            + "可传参数：<br>"
            + "flag 某张图片的标志  String <br>")
    @PostMapping("/queryImageByFlag")
    @ResponseBody
    public String queryImageByFlag(@RequestParam String flag){
        return  imageDataService.queryImageByFlag(flag);
    }

}
