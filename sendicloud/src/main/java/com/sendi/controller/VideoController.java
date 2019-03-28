package com.sendi.controller;

import com.sendi.entity.ReqBody;
import com.sendi.service.VideoService;
import com.sendi.utils.ResponseData;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;


@Api(value = "VideoController ", tags = {" 实时视频接口"}, description = "获取实时视频流")
@Controller
@RequestMapping("/stream")
public class VideoController {
    @Autowired
    private VideoService videoService;

    @ApiOperation(value = "读取设备信息", notes = "接口参数信息<br>"
            + "可传参数：<br>"
            + "snCode  sn码  String <br>"
            + "type 类型  String   <br>"
            + "content 内容  String <br>")
    @PostMapping("/push")
    @ResponseBody
    public ResponseData push(@RequestBody ReqBody reqBody) throws Exception {
        return videoService.push(reqBody);
    }
}
