package com.sendi.entity;

import lombok.Data;

import java.math.BigInteger;

@Data
public class Resource {
    private Integer id;
    private BigInteger devId;
    private String resName;
    private String displayName;
    private String unitName;
    private String unitSign;
    private Long moduleId;
    private Integer  op;
    private Integer  tp;
    private Integer state;



}
