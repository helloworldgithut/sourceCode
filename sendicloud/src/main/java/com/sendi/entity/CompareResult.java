package com.sendi.entity;

/**
 * @ClassName CompareResult
 * @description TODO
 * @Author MengfengQin
 * @Date 2019/4/24 9:22
 */
public class CompareResult {
    private String type;
    private  String flag;
    private String token;
    private String content;

    public CompareResult() {
    }

    public CompareResult(String type, String flag, String token, String content) {
        this.type = type;
        this.flag = flag;
        this.token = token;
        this.content = content;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
