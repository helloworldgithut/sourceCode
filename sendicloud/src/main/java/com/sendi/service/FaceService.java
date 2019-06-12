package com.sendi.service;

import com.sendi.entity.CompareResult;
import com.sendi.entity.ReqBody;
import com.sendi.entity.receiveImgBody;
import com.sendi.utils.ResponseData;
import org.springframework.web.multipart.MultipartFile;

/**
 * Created by fengzm on 2019/1/30.
 */
public interface FaceService {
    /**
     *发送图片给设备端
     * @param snCode
     * @param file
     * @return
     * @throws Exception
     */

     ResponseData sendImg(String snCode, MultipartFile file) throws Exception;

    /**
     *开始/停止 比对 （图像与视频流比对）
     * @param snCode
     * @param content
     * @return
     * @throws Exception
     */
    ResponseData  stop(String snCode, String type, String content) throws  Exception;

    ResponseData  start(String snCode, String content,String token, MultipartFile file) throws  Exception;

//    ResponseData  start( String snCode,  String content) throws  Exception;
    /**
     *拉流接口，拼接一个拉流地址给前端
     * @param snCode
     * @param content
     * @return
     * @throws Exception
     */
    ResponseData  pushStream(String snCode, String content) throws Exception;

    ResponseData  takePhoto(String snCode, String content) throws Exception;

    void receiveImage(receiveImgBody revBody);

    void receiveResponse(CompareResult compareResult);
}
