package com.sendi.service.impl;

import com.sendi.config.Msg;
import com.sendi.config.TriggerPool;
import com.sendi.dao.TriggerDaoI;
import com.sendi.dao.TriggerOperationDaoI;
import com.sendi.entity.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class TriggerServiceImpl {
    private static Logger logger = LoggerFactory.getLogger(TriggerServiceImpl.class);
    @Autowired
    private TriggerDaoI triggerDaoI;
    @Autowired
    private TriggerOperationDaoI triggerOperationDaoI;

    public List<Trigger> queryAll(){
        List<Trigger> list = triggerDaoI.queryAll();
        return list;
    }

    public SimpleResponse delete(Integer id){
        logger.info("触发器删除：前端发来的请求：triggerId:"+id);
        SimpleResponse response =new SimpleResponse();
        if(TriggerPool.trigResMap.containsKey(id)){
            int resId = TriggerPool.trigResMap.get(id);
            TriggerPool.removeTrigger(resId);
            response.setResult(true);
            response.setMsg("删除成功");
        }else {
            for(int tri: TriggerPool.trigResMap.keySet()){
                logger.info("TrigResMap   key:"+tri+" ,value:" +TriggerPool.trigResMap.get(tri));
            }
            response.setResult(false);
            response.setMsg("删除失败");
        }
//        if(Objects.nonNull(trigger)){
//            Integer resId = trigger.getOpertionResId();
//            logger.info("resid:"+resId+",value:"+TriggerPool.triggers.get(resId));
////            TriggerPool.triggers.remove(resId);
//            TriggerPool.removeTrigger(resId);
//            response.setResult(true);
//            response.setMsg("删除成功");
//            for(Integer res : TriggerPool.triggers.keySet()){
//                logger.info("key:"+res+",value:"+TriggerPool.triggers.get(res));
//            }
//        }
        logger.info("删除触发器"+response);
        return response;
    }

    public SimpleResponse insertOrUpdate(Integer id) {
        logger.info("触发器插入/更新：前端发来的请求："+id);
        Trigger trigger = triggerDaoI.queryById(id);
        SimpleResponse response = new SimpleResponse();

        if (Objects.nonNull(trigger)) {
//            Integer resId = trigger.getOpertionResId();
//            TriggerPool.triggers.remove(resId);
            TriggerDto dto = new TriggerDto();
            dto.setTriggerId(trigger.getId());
            dto.setTriggerResId(trigger.getOpertionResId());
            dto.setExecuteCmd(trigger.getExecuteCmdContent());
            dto.setExecuteCmdType(trigger.getExecuteCmdType());
            dto.setExecuteResName(trigger.getExecuteResName());
            dto.setDataType(trigger.getDataType());
            dto.setState(trigger.getState());
            dto.setExecuteDevID(trigger.getExecuteDevId());

            //触发值
            List<TriggerOperation> operations = triggerOperationDaoI.queryByTrigId(trigger.getId());
            List<TriggerVal> triggerVals = new ArrayList<>();
            for (TriggerOperation operation : operations) {
                TriggerVal val = new TriggerVal();
                val.setOp(operation.getOptType());
                val.setVal(operation.getOptContent());
                triggerVals.add(val);
            }
            dto.setTriggerVals(triggerVals);
            TriggerPool.isExecute.put(dto.getTriggerResId(),false);
            //删除之前缓存
//            if(TriggerPool.triggers.containsKey(resId)){
//                TriggerPool.removeTrigger(resId);
//                logger.info("先删除triggers缓存");
//            }
//            if(TriggerPool.trigResMap.containsKey(trigger.getId())){
//                TriggerPool.trigResMap.remove(trigger.getId());
//                logger.info("先删除trigResMap缓存");
//            }
            //把新的添加到缓存中
            TriggerPool.addTrigger(dto.getTriggerResId(), dto);
            TriggerPool.trigResMap.put(trigger.getId(),dto.getTriggerResId());
            response.setResult(true);
            response.setMsg("新增/更新成功");
        }else {
            logger.info("trigger是空null的====>"+trigger);
            response = new SimpleResponse(false, "新增/更新失败");
        }
        logger.info("新增/更新"+response);
        return response;
    }

    public SimpleResponse updateStateById(int triggerId, int state){
        logger.info("触发器开关：前端发来的请求：triggerId:"+triggerId+" ,state:"+state);
        SimpleResponse response = new SimpleResponse();
        try {
            int resId = TriggerPool.trigResMap.get(triggerId);
            TriggerPool.updateTriggerState(resId,state);
            response.setResult(true);
            response.setMsg("更新触发器状态为"+state+"成功");
            if(state == Msg.TRIGGER_STATE_CLOSE){
                TriggerPool.isExecute.put(resId,true);
                logger.info(response.toString());
            }else if(state == Msg.TRIGGER_STATE_OPEN){
                TriggerPool.isExecute.put(resId,false);
                logger.info(response.toString());
            }else {
                logger.info("开关状态有误");
            }
        }catch (Exception e){
            response.setResult(false);
            response.setMsg("更新触发器状态为"+state+"失败");
            logger.info(e.getMessage());
            logger.info(response.toString());
        }
        return response;
    }

    public SimpleResponse updateStateByDoExecute(int triggerId, int state){
        SimpleResponse response = new SimpleResponse();
        try {
            Trigger trigger = new Trigger();
            trigger.setId(triggerId);
            trigger.setState(state);
            triggerDaoI.updateStateByDoExecute(trigger);
            response.setResult(true);
            logger.info("TriggerServiceImpl--------执行触发器成功！");
            response.setMsg("更新状态成功");
        }catch (Exception e){
            logger.info("TriggerServiceImpl--------执行触发器失败！");
            response.setResult(false);
            response.setMsg("更新状态失败");
            e.printStackTrace();
        }
        return  response;
    }
}
