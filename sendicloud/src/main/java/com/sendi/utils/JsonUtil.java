package com.sendi.utils;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;

import java.util.Map;
/***
    * @Author Mengfeng Qin
    * @Description Json工具类
    * @Date 2019/4/1 18:29
*/
public class JsonUtil {

    public static String toJSONString(Object o) {
        if (o instanceof String) {
            return (String) o;
        }

        return JSON.toJSONString(o, SerializerFeature.WriteMapNullValue, SerializerFeature.WriteNullStringAsEmpty,
                SerializerFeature.WriteNullNumberAsZero, SerializerFeature.WriteNullBooleanAsFalse,
                SerializerFeature.WriteDateUseDateFormat, SerializerFeature.DisableCircularReferenceDetect);
    }

    public static JSONObject fromJson(String text) {
        return JSONObject.parseObject(text);
    }


    public static Object parseObject(String text, Class clazz) {
        Object result = JSON.parseObject(text, clazz);
        return result;
    }

    public static Map<String, Object> jsonToMap(String json) {
        if (StringUtils.isEmpty(json)) {
            return null;
        }
        return JSON.parseObject(json, Map.class);
    }

    public static byte[] toJsonByte(Object bean) {
        return toJSONString(bean).getBytes();
    }

}
