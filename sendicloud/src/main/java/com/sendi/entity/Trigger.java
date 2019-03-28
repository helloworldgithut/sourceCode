package com.sendi.entity;

import lombok.Data;

import java.util.Date;

@Data
public class Trigger {

    private Integer id;
    //触发器名称
    private String trigName;
    //创建时间
    private Date createTime;
    //用户ID
    private String userId;
    //产品ID
    private Integer proId;
    //触发器条件类型
    private String operationType;
    //关联应用ID
    private Integer appId;
    //触发时执行的设备ID
    private Long executeDevId;
    //触发时执行指令类型 read/write/execute
    private String executeCmdType;
    //触发执行资源ID
    private Integer executeResId;
    //触发时执行资源
    private String executeResName;
    //触发执行内容 start/stop/clear/delete....
    private String executeCmdContent;
    //触发资源ID
    private Integer opertionResId;
    //触发设备ID
    private Long opertionDevId;
    //00 可忽略消息、01 数值，包括整型、浮点(暂时只有数值类型有触发器)、02 字符串、03 图片、04 权限
    private String dataType;
    //触发器开关状态
    private  int state;

}
