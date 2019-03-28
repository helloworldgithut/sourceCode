package com.sendi.entity;

import java.util.List;

public class TriggerDto {
    //触发器ID
    private Integer triggerId;
    //触发资源的id
    private Integer triggerResId;
    //执行触发的设备ID
    private Long executeDevID;
    //执行触发的资源名称
    private String executeResName;
    //执行命令 start/stop
    private String executeCmd;
    //执行命令类型  read/write/execute
    private String executeCmdType;
    //触发器条件类型  00,01,02,03,04
    private String dataType;
    //触发器状态 0 / 1
    private Integer state;
    //触发阀值    10<X<20 、 X<10 或 X>20
    private List<TriggerVal> triggerVals;

    public Integer getTriggerId() {        return triggerId;    }

    public void setTriggerId(Integer triggerId) {        this.triggerId = triggerId;    }

    public Integer getTriggerResId() {
        return triggerResId;
    }

    public void setTriggerResId(Integer triggerResId) {
        this.triggerResId = triggerResId;
    }

    public Long getExecuteDevID() {
        return executeDevID;
    }

    public void setExecuteDevID(Long executeDevID) {
        this.executeDevID = executeDevID;
    }

    public String getExecuteResName() {
        return executeResName;
    }

    public void setExecuteResName(String executeResName) {
        this.executeResName = executeResName;
    }

    public String getExecuteCmd() {
        return executeCmd;
    }

    public void setExecuteCmd(String executeCmd) {
        this.executeCmd = executeCmd;
    }

    public String getExecuteCmdType() {
        return executeCmdType;
    }

    public void setExecuteCmdType(String executeCmdType) {
        this.executeCmdType = executeCmdType;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public List<TriggerVal> getTriggerVals() {
        return triggerVals;
    }

    public Integer getState() {        return state;    }

    public void setState(Integer state) {        this.state = state;    }

    public void setTriggerVals(List<TriggerVal> triggerVals) {
        this.triggerVals = triggerVals;
    }

}
