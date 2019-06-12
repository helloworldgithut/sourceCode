package com.sendi.utils;

import com.alibaba.fastjson.JSON;
import lombok.Cleanup;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.math.BigInteger;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class HTTPUtil {

    private static Logger logger = LoggerFactory.getLogger(HTTPUtil.class);

    public static String okHttpPost(RequestBody requestBody, String url){
        OkHttpClient client = new OkHttpClient.Builder()
                .retryOnConnectionFailure(true)
                .connectTimeout(10, TimeUnit.SECONDS) //连接超时
                .readTimeout(10, TimeUnit.SECONDS) //读取超时
                .writeTimeout(10, TimeUnit.SECONDS) //写超时
                .build();
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();

        Response response;
        try {
            response = client.newCall(request).execute();
            if(response.isSuccessful()){
                return response.body().string();
            }

        } catch (IOException e) {
        }
        return null;
    }
    public static String sendPostJson(String host, int port, String path, String data,int timeout) throws IOException {

        SocketAddress dest = new InetSocketAddress(host, port);

        Socket socket = new Socket();
        socket.setSoTimeout(timeout);
        socket.connect(dest);

        OutputStreamWriter streamWriter = new OutputStreamWriter(socket.getOutputStream(), "utf-8");
        BufferedWriter bufferedWriter = new BufferedWriter(streamWriter);
        bufferedWriter.write("POST " + path + " HTTP/1.1\r\n");
        bufferedWriter.write("Host: " + host + "\r\n");
        bufferedWriter.write("Content-Length: " + data.length() + "\r\n");
        bufferedWriter.write("Connection:" + "keep-alive" + "\r\n");
        bufferedWriter.write("Content-Type: application/json;\r\n");
        bufferedWriter.write("\r\n");
        bufferedWriter.write(data);
        bufferedWriter.flush();
        bufferedWriter.write("\r\n");
        bufferedWriter.flush();
        @Cleanup
        BufferedInputStream streamReader = new BufferedInputStream(socket.getInputStream());
        @Cleanup
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(streamReader, "utf-8"));
        String line = null;
        StringBuffer sb = new StringBuffer();
        new BufferedInputStream(socket.getInputStream());
        while ((line = bufferedReader.readLine()) != null) {
            if (line.startsWith("{")) {
                sb.append(line);
                break;
            }
        }
        return sb.toString();
    }

    public static String getResponse(BigInteger devID, BigInteger state){
        Map<String ,BigInteger> dataMap = new HashMap<>();
        dataMap.put("id",devID);
        dataMap.put("state",state);
        String dataStr = JSON.toJSONString(dataMap);
        try {
            String respon = HTTPUtil.sendPostJson("127.0.0.1",8080,"/iot_aid/device/status", dataStr,15000);
//                    String response = HTTPUtil.sendPostJson("192.168.60.137",8080,"iot_aid/device/status", dataStr,15000);
            logger.info("前端返回："+respon);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }



//    public static void main(String[] args) {
//        File file = new File("");
//        RequestBody requestBody = new MultipartBody.Builder()
//                .setType(MultipartBody.FORM)
//                .addFormDataPart("token", "00000000300b8fbc")
//                .addFormDataPart("flag", "1778526768")
//                .build();
//        HTTPUtil httpUtil = new HTTPUtil();
//        String resultText =httpUtil.okHttpPost(requestBody,"http://192.168.60.137:8080/iot_aid/photo/show/import");
//        JSONObject.parseObject(resultText);
//    }
}
