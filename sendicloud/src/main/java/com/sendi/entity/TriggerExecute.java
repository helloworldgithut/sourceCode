package com.sendi.entity;

import lombok.Data;

import java.math.BigInteger;

@Data
public class TriggerExecute {
    private Integer id;
    private BigInteger devId;
    private String cmdType;
    private Integer resId;
    private String resName;
    private String cmdContent;
    private Integer trigId;
}
