package com.sendi.entity;

import lombok.Data;

import java.math.BigInteger;

@Data
public class TriggerResourceMerge {
    /* ID 主键 、自动递增 */
    private Integer id;
    /* 触发器ID */
    private Integer trigId;
    /* 设备ID */
    private BigInteger devId;
    /* 资源ID */
    private BigInteger resId;
}
