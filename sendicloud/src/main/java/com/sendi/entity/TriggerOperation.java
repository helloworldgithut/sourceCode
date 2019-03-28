package com.sendi.entity;

import lombok.Data;

@Data
public class TriggerOperation {

    private Integer id;
    //阈值 10
    private String optContent;
    // 条件 >/  </  =/  >=/  <=
    private String optType;
    // 触发器ID
    private Integer trigId;
}
