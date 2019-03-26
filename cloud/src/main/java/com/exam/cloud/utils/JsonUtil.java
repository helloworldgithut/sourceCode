package com.exam.cloud.utils;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;

import java.util.Map;

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

//    public static void main(String[] args) {
//        byte[] a = JsonUtil.toJsonByte(new Coordinate(2, 0));
//        System.out.println(JsonUtil.toJSONString(new Coordinate(2, 0)));
//        Coordinate c = (Coordinate) JsonUtil.parseObject(new String(a), Coordinate.class);
//        System.out.println(c);
//    }

//    public static BodyObj resolveBody(String body){
//        JSONObject jsonMsg;
//        BodyObj obj = new BodyObj();
//        try {
//            jsonMsg = new JSONObject();
//            obj.setType(jsonMsg.getString("type"));
//            obj.setImei(jsonMsg.getString("imei"));
//            obj.setMsg(jsonMsg.getString("msg"));
//
//            return obj;
//        } catch (JSONException e) {
//            e.printStackTrace();
//            return obj;
//        }
//    }

    public static class BodyObj {
        private Object msg;
        private String type;
        private String imei;

        public Object getMsg() {
            return msg;
        }

        public void setMsg(Object msg) {
            this.msg = msg;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getImei() {
            return imei;
        }

        public void setImei(String imei) {
            this.imei = imei;
        }

        @Override
        public String toString() {
            return "{" + "msg:" + this.msg + ", type:" + this.type  +", imei:" + this.imei + '}';
        }
    }
}
