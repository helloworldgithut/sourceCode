package com.sendi.config;

import com.sendi.entity.DeviceInstructions;
import com.sendi.entity.TriggerDto;
import com.sendi.entity.TriggerVal;
import com.sendi.service.DeviceInstructService;
import com.sendi.service.impl.TriggerServiceImpl;
import com.sendi.utils.ApplicationContextProvider;
import com.sendi.utils.ResponseData;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
/***
    * @Author Mengfeng Qin
    * @Description
    * @Date 2019/3/26 18:20
*/
public  class TriggerPool {
    private static Logger logger = LoggerFactory.getLogger(TriggerPool.class);
    public static Map<Integer, TriggerDto> triggers = new HashMap<>();//触发器缓存 resId,dto
    public static Map<Integer, Boolean> isExecute = new HashMap<>();//判断是否已经执行过 resId,boolean
    public static Map<Integer,Integer> triggerState = new HashMap<>();//触发器状态resId,state
    public static Map<Integer,Integer> trigResMap = new HashMap<>();//触发器ID跟资源ID绑定triggerId, resId

    private static DeviceInstructService deviceInstructService = ApplicationContextProvider.getBean(DeviceInstructService.class);
    private static TriggerServiceImpl triggerService = ApplicationContextProvider.getBean(TriggerServiceImpl.class);
    /**
     * 阀值处理过滤函数
     * 默认都是执行函数
     */
    public static void thresholdFilter(int resId, BigDecimal value) {

        if(!isTriggerOpen(resId)){
            logger.info("触发器处于关闭状态,resID"+resId+" ,value"+value);
            return;
        }
        //触发器被执行过，直接返回，不再执行（初始是false,执行过后为true）
        if (isExecute.get(resId)){
            logger.info("触发器已经触发过一次了，不再触发,resID"+resId+" ,value"+value);
            return;
        }

        TriggerDto triggerDto = triggers.get(resId);

//        for(Integer res: triggers.keySet()){
//            logger.info("trigger缓存中的resid:"+res+" ,value:"+triggers.get(res));
//        }
        if (Objects.isNull(triggerDto)) return;

        logger.info(resId + "触发器过滤数据: " + triggerDto.getExecuteDevID());
        //过滤掉非数字类型的触发器
        if (!triggerDto.getDataType().equals(Msg.TYPE_01)){
            logger.info("触发器数据类型不是数值类型,resID"+resId+" ,value"+value);
            return;
        }

        //获取执行命令
        List<TriggerVal> triggerVals = triggerDto.getTriggerVals();
        String cmdType = triggerDto.getExecuteCmdType();
        String resName = triggerDto.getExecuteResName();
        String cmdContent = triggerDto.getExecuteCmd();
        BigInteger devId = BigInteger.valueOf(triggerDto.getExecuteDevID());
        // read /write /execute
        if (StringUtils.isEmpty(cmdType)) return;

        //对哪个资源下发什么指令
        DeviceInstructions deviceInstructions = new DeviceInstructions();
        deviceInstructions.setDevId(devId);
        deviceInstructions.setHandleType(cmdType);
        deviceInstructions.setResName(resName);
        deviceInstructions.setContent(cmdContent);


        //遍历触发值，判断是否触发指令下发
        if (triggerVals.size() > 0) {
            boolean  doExecute = doExecute(triggerVals, value);
            if (doExecute) {
                logger.info("触发器执行的命令 DeviceInstructions：：："+deviceInstructions);
                sendCMD(deviceInstructions, resId);
                triggerService.updateStateByDoExecute(triggerDto.getTriggerId(), Msg.TRIGGER_STATE_CLOSE);
                TriggerPool.triggerState.put(resId, Msg.TRIGGER_STATE_CLOSE);
            }
        }
    }


    /**
     * 触发器触发后发送相应的指令
     */
    public static void sendCMD(DeviceInstructions deviceInstructions, Integer resId) {
//            TriggerExecute triggerExecute = triggerExecuteService.queryByTrigId(trigId);
//
//        DeviceInstructions deviceInstructions = new DeviceInstructions();
//        deviceInstructions.setDevId(triggerExecute.getDevId());
//        deviceInstructions.setResName(triggerExecute.getResName());
//        deviceInstructions.setHandleType(triggerExecute.getCmdType());
//        deviceInstructions.setContent(triggerExecute.getCmdContent());

//        logger.info("自动注入："+deviceInstructService);
        if( deviceInstructions.getHandleType().equals(Msg.READ)){
            try {
                ResponseData responseData = deviceInstructService.readResource(deviceInstructions);
                isExecute.put(resId,true);
                logger.info("触发器读取"+responseData.getMsg());
            } catch (Exception e) {
                logger.info("触发器执行失败！");
                e.printStackTrace();
            }
        }else if(deviceInstructions.getHandleType().equals(Msg.WRITE)){
            try {
                ResponseData responseData = deviceInstructService.writeResource(deviceInstructions);
                isExecute.put(resId,true);
                logger.info("触发器写入"+responseData.getMsg());
            } catch (Exception e) {
                logger.info("触发器执行失败！！");
                e.printStackTrace();
            }
        } else if(deviceInstructions.getHandleType().equals(Msg.EXECUTE)){
            try {
                ResponseData responseData = deviceInstructService.excuteResource(deviceInstructions);
                isExecute.put(resId,true);
                logger.info("触发器执行"+responseData.getMsg());
            } catch (Exception e) {
                logger.info("触发器执行失败！！！");
                e.printStackTrace();
            }
        }
    }

    public static synchronized void addTrigger(Integer resId, TriggerDto triggerDto) {
        triggers.put(resId, triggerDto);
        triggerState.put(resId, triggerDto.getState());
    }
    public static synchronized void removeTrigger(Integer resId) {
        triggers.remove(resId);
        triggerState.remove(resId);
        isExecute.remove(resId);
        List<Integer> list = new ArrayList<>();
        for(int key: trigResMap.keySet()){
            if(trigResMap.get(key).equals(resId)){
                list.add(key);
            }
        }
        for(int key : list){
            trigResMap.remove(key);
        }
    }

    public static synchronized void updateTriggerState(int resId, int state) {
        triggerState.put(resId, state);
    }

    public static boolean isTriggerOpen(Integer resId) {
        Integer state = triggerState.get(resId);
        return state != null && state == Msg.TRIGGER_STATE_OPEN;
    }


    public static  boolean doExecute(List<TriggerVal> triggerVals,BigDecimal value){
        //判断触发条件是否有交集
        boolean isIntersection = isIntersection(triggerVals);

        Boolean doExecute = null;

        for (TriggerVal triggerVal : triggerVals) {
            String op = triggerVal.getOp();
            String val = triggerVal.getVal();
            boolean isTrigger;
            if (StringUtils.isNotEmpty(op) && StringUtils.isNotEmpty(val)) {
                BigDecimal tarVal = new BigDecimal(val);

                int result = value.compareTo(tarVal);
                if (op.equals("==") && result == 0) {
//                    sendCMD(deviceInstructions, resId);
                    isTrigger  = true;
                } else if (op.equals(">") && result > 0) {
//                    sendCMD(deviceInstructions, resId);
                    isTrigger  = true;
                } else if (op.equals(">=") && result >= 0) {
//                    sendCMD(deviceInstructions, resId);
                    isTrigger  = true;
                } else if (op.equals("<") && result < 0) {
//                    sendCMD(deviceInstructions, resId);
                    isTrigger  = true;
                } else if (op.equals("<=") && result <= 0) {
                    isTrigger  = true;
//                    sendCMD(deviceInstructions, resId);
                }else {
                    isTrigger =false;
                }

                //第一次执行
                if (doExecute == null) {
                    doExecute = isTrigger;
                }
                //不是第一次执行
                else {
                    if (isIntersection) {
                        doExecute = doExecute && isTrigger;
                    } else {
                        doExecute = doExecute || isTrigger;
                    }
                }
            }
        }
        logger.debug("校验结果为 -> {}", doExecute);
        if (doExecute == null) return false;
        return doExecute;
    }




    /**
     * 双值比较，判断触发条件是否有交集
     *
     * @param triggerVals 判断触发条件是否有交集
     * @return
     */
    private static boolean isIntersection(List<TriggerVal> triggerVals) {
        if (triggerVals == null || triggerVals.size() < 2) return false;
        TriggerVal val1 = triggerVals.get(0);
        TriggerVal val2 = triggerVals.get(1);
        BigDecimal leVal = null;
        BigDecimal geVal = null;
        Map<String, String> valVsOp = new HashMap<>();

        if (val1.getOp().equals(">")
                || val1.getOp().equals(">=")) {
            geVal = new BigDecimal(val1.getVal());
            valVsOp.put("geVal", val1.getOp());
        } else {
            leVal = new BigDecimal(val1.getVal());
            valVsOp.put("leVal", val1.getOp());
        }

        if (val2.getOp().equals(">")
                || val2.getOp().equals(">=")) {
            geVal = new BigDecimal(val2.getVal());
            valVsOp.put("geVal", val2.getOp());
        } else {
            leVal = new BigDecimal(val2.getVal());
            valVsOp.put("leVal", val2.getOp());
        }

        if (valVsOp.size() < 2) return false;
        int result = leVal.compareTo(geVal);
        return result > 0;

    }

}
