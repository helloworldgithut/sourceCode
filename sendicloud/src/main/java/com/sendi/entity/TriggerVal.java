package com.sendi.entity;


public class TriggerVal {
    //  触发条件 >/ >=/ =/ </ <=
    private String op;
    //触发值
    private String val;

    public String getOp() {
        return op;
    }

    public void setOp(String op) {
        this.op = op;
    }

    public String getVal() {
        return val;
    }

    public void setVal(String val) {
        this.val = val;
    }
}
