package com.sendi.entity;

import lombok.Data;

import java.math.BigInteger;
import java.sql.Timestamp;

@Data
public class ImageData {
    private Integer id;
    private BigInteger devId;
    private Integer  resId;
    private Integer sortId;
    private String valued;
    private String flag;
    private Timestamp sendTime;

}
