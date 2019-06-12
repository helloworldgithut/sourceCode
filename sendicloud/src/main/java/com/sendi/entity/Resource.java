package com.sendi.entity;

import lombok.Data;

import java.math.BigInteger;

@Data
public class Resource {
    private Integer id;
    private BigInteger devId;
    private String resName;
    private String displayName;
    private Integer  op;
    private Integer  tp;
    private Integer state;
    private Integer type;



}
