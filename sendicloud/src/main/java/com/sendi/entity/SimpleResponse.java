package com.sendi.entity;

public class SimpleResponse {
    private boolean result;
    private String msg;

    public SimpleResponse() {
    }

    public SimpleResponse(boolean result, String msg) {
        this.result = result;
        this.msg = msg;
    }

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    @Override
    public String toString() {
        return "SimpleResponse{" +
                "result=" + result +
                ", msg='" + msg + '\'' +
                '}';
    }
}
