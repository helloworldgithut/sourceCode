package com.sendi.service;

import com.sendi.entity.ReqBody;
import com.sendi.utils.ResponseData;

/**
 *视频接口
 * Created by fengzm on 2019/1/31.
 */
public interface VideoService {
    /**
     *实时视频
     * @param reqBody
     * @return
     * @throws Exception
     */
    ResponseData push(ReqBody reqBody) throws Exception;


}
