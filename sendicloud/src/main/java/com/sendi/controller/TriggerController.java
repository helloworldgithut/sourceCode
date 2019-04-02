package com.sendi.controller;

import com.sendi.config.TriggerPool;
import com.sendi.entity.SimpleResponse;
import com.sendi.service.impl.TriggerServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
/***
    * @Author Mengfeng Qin
    * @Description 触发器新增、更新、删除、更改开关状态接口
    * @Date 2019/3/29 10:53
*/
@Api(value = "TriggerController ", tags = {"前端通知触发器新增/更新、删除接口"}, description = "新增/更新、删除")
@RestController
@RequestMapping("/coap/trigger")
public class TriggerController {
private  static org.slf4j.Logger log = LoggerFactory.getLogger(TriggerController.class);
    @Autowired
    private TriggerServiceImpl triggerService;

    @ApiOperation(value = "新增/更新触发器信息", notes = "接口参数信息<br>"
            + "传入参数：<br>"
            + "triggerId  触发器ID  int <br>")
    @PostMapping("/updateInsertTrigger")
    public SimpleResponse insertTrigger(int triggerId){
        return triggerService.insertOrUpdate(triggerId);
    }

    @ApiOperation(value = "删除触发器信息", notes = "接口参数信息<br>"
            + "传入参数：<br>"
            + "triggerId  触发器ID  int <br>")
    @PostMapping("/removeTrigger")
    public SimpleResponse deleteTrigger(int triggerId){
      return triggerService.delete(triggerId);
    }

    @ApiOperation(value = "更新触发器开关量", notes = "接口参数信息<br>"
            + "传入参数：<br>"
            + "triggerId  触发器ID  int <br>"
            + "state  触发器开关状态  int <br>")
    @PostMapping("/updateTriggerState")
    public SimpleResponse updateState(int triggerId, int state){
        return triggerService.updateStateById(triggerId,state);
    }

    @ApiOperation(value = "测试查看缓存中的数据")
    @GetMapping("/queryTriggerPool")
    public void queryMqttPool(){
        log.info("资源-触发器列表：{}",TriggerPool.triggers.toString());
        log.info("触发器id-资源id列表：{}",TriggerPool.isExecute.toString());
        log.info("资源id-触发器状态列表：{}",TriggerPool.triggerState.toString());
    }
}
