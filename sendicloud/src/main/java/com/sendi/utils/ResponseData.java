package com.sendi.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
public class ResponseData<T> {
    private boolean result ;
    private String msg;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T data;

    /**
     *返回成功模板
     * @param content
     * @return
     */
    public static ResponseData success(String content){
        ResponseData responseData = new ResponseData();
        responseData.setData(content);
        responseData.setResult(true);
        responseData.setMsg("操作成功");
        return responseData;
    }

    /**
     *返回失败模板
     * @param msg
     * @return
     */
    public static ResponseData fail(String msg){
        ResponseData responseData = new ResponseData();
        responseData.setResult(false);
        responseData.setMsg(msg);
        return responseData;
    }

}
