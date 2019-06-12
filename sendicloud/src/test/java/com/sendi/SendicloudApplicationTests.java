package com.sendi;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sendi.entity.CompareResult;
import com.sendi.utils.HTTPUtil;
import com.sendi.utils.RedisUtil;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SendicloudApplicationTests {

    @Autowired
    private RedisUtil redisUtil;
//
    @Test
    public void contextLoads() {
    }

    @Test
    public void  testRedisString(){
        redisUtil.set("testiot","test1231",30);
        System.out.println(redisUtil.get("testiot"));
        System.out.println(redisUtil.hasKey("testiot"));
    }

//    @Test
//    public void testFace() throws IOException {
//        Map<String, String> dataMap = new HashMap<>();
//        dataMap.put("token", "MTU1NjQxNjY5NjM4NDt7InJvbGVJZCI6IjQwMjhiYzA5NjkyNDU5MDQwMTY5Mjc3NjI1N2MwMDAyIiwicm9sZU5hbWUiOiLlrabnlJ8iLCJ1c2VyTmFtZSI6IuaYjuaYjiIsInVzZXJJZCI6IjQwMjhiYzA5NmEyNDQxNzUwMTZhMjVkMzg1MTkwMDE0In0=");
//        dataMap.put("info","该图片不包含人脸，比对素材无效");
//        String dataStr = JSON.toJSONString(dataMap);
//        System.out.println(dataStr);
//        String response = HTTPUtil.sendPostJson("192.168.60.137", 8080, "/iot_aid/experiment/rollback", dataStr, 15000);
//        System.out.println(response);

//        RequestBody requestBody = new MultipartBody.Builder()
//                .setType(MultipartBody.FORM)
//                .addFormDataPart("info", "该图片不包含人脸，比对素材无效")
//                .addFormDataPart("token", "MTU1NjQxNjY5NjM4NDt7InJvbGVJZCI6IjQwMjhiYzA5NjkyNDU5MDQwMTY5Mjc3NjI1N2MwMDAyIiwicm9sZU5hbWUiOiLlrabnlJ8iLCJ1c2VyTmFtZSI6IuaYjuaYjiIsInVzZXJJZCI6IjQwMjhiYzA5NmEyNDQxNzUwMTZhMjVkMzg1MTkwMDE0In0=")
//                .build();
//
//        HTTPUtil httpUtil = new HTTPUtil();
//        String resultText =httpUtil.okHttpPost(requestBody,"http://192.168.60.137:8080/iot_aid/experiment/rollback");
//        JSONObject.parseObject(resultText);
//
//        System.out.println(resultText);
//    }
}
