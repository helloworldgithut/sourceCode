package com.sendi.entity;

import lombok.Data;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.Date;

@Data
public class Device {
    /**
     * 设备id
     */
    private BigInteger id;
    private Integer proId;
    private String devName;
    private String imei;
    private Integer state;
    private Integer dataSecret;
    private String description;
    private Date createTime;
    private String devLogo;
    private String devLocation;
    private Double longitude;
    private Double latitude;
    private String snCode;
    private Integer displayType;
    /**
     * 设备存活时间
     */
    private Integer lifeTime;
    private String ip;
    private Integer port;
    private Timestamp acceptTime;
    private String modId;
    private Integer attribute;
}
