package com.sendi.controller;

import com.sendi.entity.ReqBody;
import com.sendi.entity.receiveImgBody;
import com.sendi.service.FaceService;
import com.sendi.service.impl.DeviceService;
import com.sendi.service.impl.ImageDataService;
import com.sendi.utils.ResponseData;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Api(value = "FaceController ", tags = {" 人脸识别接口"}, description = "拍照、发送图片、对比、拉流")
@Controller
@RequestMapping("/face")
public class FaceController {
    private static final Logger logger = LoggerFactory.getLogger(FaceController.class);

    @Autowired
    private FaceService faceService;
    @Autowired
    private DeviceService deviceService;
    @Autowired
    private ImageDataService imageDataService;

    @ApiOperation(value = "发送拍照指令给设备端", notes = "接口参数信息<br>"
            + "可传参数：<br>"
            + "snCode  sn码  String <br>"
            + "content 指令  String <br>")
    @PostMapping("/takePhoto")
    @ResponseBody
//    public ResponseData takePhoto(@RequestParam String snCode, @RequestParam String content) throws Exception {
    public ResponseData takePhoto(@RequestBody ReqBody reqBody) throws Exception {
        logger.info("前端发送拍照指令 reqBody"+reqBody);
        String snCode = reqBody.getSnCode();
        String type = reqBody.getType();
        String content = reqBody.getContent();
        return  faceService.takePhoto(snCode, content);
    }

    @ApiOperation(value = "发送图片给设备端", notes = "接口参数信息<br>"
            + "可传参数：<br>"
            + "snCode  sn码  String <br>"
            + "file 图片文件  MultipartFile <br>")
    @PostMapping("/sendImg")
    @ResponseBody
    public ResponseData sendImg(@RequestParam String snCode, @RequestParam MultipartFile file) throws Exception {
         return  faceService.sendImg(snCode,file);
    }


    @ApiOperation(value = "结束 比对 （图像与视频流比对）", notes = "接口参数信息<br>"
            + "可传参数：<br>"
            + "snCode  sn码  String <br>"
            + "content 操作内容  String   start/<br>")
    @PostMapping("/stop")
    @ResponseBody
    public ResponseData  stop(@RequestBody ReqBody reqBody) throws  Exception{
//    public ResponseData  active(@RequestParam String snCode, @RequestParam String content) throws  Exception{
        logger.info("前端发送 结束 比对 "+reqBody);
        String snCode = reqBody.getSnCode();
        String type = reqBody.getType();
        String content = reqBody.getContent();
        return  faceService.stop(snCode,type,content);
    }
//    public ResponseData  active(@RequestBody ReqBody reqBody) throws  Exception{
//        logger.info("前端开始/结束比对 active"+reqBody);
//        String snCode = reqBody.getSnCode();
//        String content = reqBody.getContent();
//        return  faceService.active(snCode,content);
//    }

    @ApiOperation(value = "开始比对 （图像与视频流比对）", notes = "接口参数信息<br>"
            + "可传参数：<br>"
            + "snCode  sn码  String <br>"
            + "content 操作内容  String   start/<br>"
            + "file 图片文件  MultipartFile <br>")
    @PostMapping("/start")
    @ResponseBody
    public ResponseData  start(@RequestParam String snCode, @RequestParam String content,
                @RequestParam MultipartFile file) throws  Exception{
        logger.info("前端开始比对 start "+snCode+"_"+content);
        return  faceService.start(snCode, content, file);
    }

    @ApiOperation(value = " 拉流接口，拼接一个拉流地址给前端", notes = "接口参数信息<br>"
            + "可传参数：<br>"
            + "snCode  sn码  String <br>"
            + "content 操作内容  String   push/pull <br>")
    @PostMapping("/pullStream")
    @ResponseBody
    public ResponseData  pushStream(@RequestBody ReqBody reqBody) throws Exception{
//        logger.info("前端发来的请求 pushStream "+snCode+", "+content);
        String snCode = reqBody.getSnCode();
        String type = reqBody.getType();
        String content = reqBody.getContent();
        return  faceService.pushStream(snCode, content);
    }



    @ApiOperation(value = " 接收图片接口，接收设备端发来的Base64图片", notes = "接口参数信息<br>"
            + "可传参数：<br>"
            + "snCode  sn码  String <br>"
            + "content 操作内容  String   push/pull <br>")
    @PostMapping("/receiveImage")
    @ResponseBody
    public void  receiveImage(@RequestBody receiveImgBody revBody) throws Exception {
//    public ResponseData  pushStream(@RequestParam String snCode, @RequestParam String content) throws Exception{
        logger.info("设备端发来图片数据 pushStream " + revBody.getFlag());
        faceService.receiveImage(revBody);
    }

//    @ApiOperation(value = " 接收图片接口，接收设备端发来的Base64图片", notes = "接口参数信息<br>"
//            + "可传参数：<br>"
//            + "snCode  sn码  String <br>"
//            + "content 操作内容  String   push/pull <br>")
//    @PostMapping("/receiveImage")
//    @ResponseBody
//    public String  receiveImage(@RequestParam String hash_result, @RequestParam String webtoken,
//                                @RequestParam String flag, @RequestParam MultipartFile file) throws Exception {
//        logger.info("**设备端发送图片*********" + file.getOriginalFilename());
//
//
//
//        return  "";
//    }


}
