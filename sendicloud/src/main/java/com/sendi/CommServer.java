package com.sendi;

import com.sendi.config.TriggerPool;
import com.sendi.entity.Trigger;
import com.sendi.entity.TriggerDto;
import com.sendi.entity.TriggerOperation;
import com.sendi.entity.TriggerVal;
import com.sendi.service.impl.TriggerOperationService;
import com.sendi.service.impl.TriggerServiceImpl;
import com.sendi.utils.CoapServer;
import com.sendi.utils.TCPServer;
import com.sendi.utils.TransactionServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class CommServer implements CommandLineRunner {
    private static Logger log = LoggerFactory.getLogger(CommServer.class);
    @Autowired
    private CoapServer coapServer;
    @Autowired
    private TCPServer tcpServer;
    @Autowired
    private TransactionServer transactionServer;
    @Autowired
    private TriggerServiceImpl triggerService;
    @Autowired
    private TriggerOperationService triggerOperationService;
    @Override
    public void run(String... args) throws Exception {
        Thread register = new Thread(coapServer);
        register.start();
        Thread transaction = new Thread(transactionServer);
        transaction.start();
        Thread tcp = new Thread(tcpServer);
        tcp.start();
        log.info("初始化触发器缓存");
        initTriggerPool();
    }

    private void initTriggerPool() {
        List<Trigger> Triggers = triggerService.queryAll();
        for (Trigger trig : Triggers){
            TriggerDto dto = new TriggerDto();
            dto.setTriggerId(trig.getId());
            dto.setTriggerResId(trig.getOpertionResId());
            dto.setExecuteDevID(trig.getExecuteDevId());
            dto.setExecuteCmd(trig.getExecuteCmdContent());
            dto.setExecuteCmdType(trig.getExecuteCmdType());
            dto.setExecuteResName(trig.getExecuteResName());
            dto.setState(trig.getState());
            dto.setDataType(trig.getDataType());

            List<TriggerOperation> trigOperations = triggerOperationService.queryByTrigId(trig.getId());
            List<TriggerVal> triggerVals = new ArrayList<>();

            for (TriggerOperation operation : trigOperations) {
                TriggerVal val = new TriggerVal();
                val.setOp(operation.getOptType());
                val.setVal(operation.getOptContent());
                triggerVals.add(val);
            }
            dto.setTriggerVals(triggerVals);
            TriggerPool.addTrigger(dto.getTriggerResId(),dto);
            TriggerPool.isExecute.put(dto.getTriggerResId(),false);
            TriggerPool.trigResMap.put(trig.getId(),dto.getTriggerResId());

        }
    }

}
