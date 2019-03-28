package com.sendi.entity;

import lombok.Data;

import java.math.BigInteger;

@Data
public class DeviceInstructions {
    /**
     * read,write,excute
     */
    private BigInteger devId;
    private String resName;
    private String handleType;
    private String content;

}


