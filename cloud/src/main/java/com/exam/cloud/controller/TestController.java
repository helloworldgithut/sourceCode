package com.exam.cloud.controller;

import com.exam.cloud.CloudApplication;
import com.exam.cloud.TcpThread;
import com.exam.cloud.utils.Byte2Base64;
import com.exam.cloud.utils.JsonUtil;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

@RestController
public class TestController {

    @PostMapping("/sendImage")
    public String sendImage(@RequestParam MultipartFile file){
        System.out.println("图片名称："+file.getOriginalFilename());
        try {
            Socket socket = TcpThread.socketMap.get("111");
            OutputStream outs = socket.getOutputStream();
            InputStream ins = file.getInputStream();
            byte[] byt = new byte[ins.available()];
            ins.read(byt);
            String imgContent = Byte2Base64.byte2Base64StringFun(byt);
            Map<String,Object>  map = new HashMap<>();
            map.put("value",imgContent);
            outs.write(JsonUtil.toJsonByte(map));
        }catch (Exception e){
            e.printStackTrace();
            return  "error";
        }
        return "ok";
    }
}
