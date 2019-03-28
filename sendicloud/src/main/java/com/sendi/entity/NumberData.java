package com.sendi.entity;

import lombok.Data;

import java.math.BigInteger;
import java.sql.Timestamp;

@Data
public class NumberData {
    private BigInteger id;
    private BigInteger devId;
    private Integer  resId;
    private Integer proId;
//    private String valued;
    private Double valued;
    private Timestamp sendTime;
    private String unit;

}
