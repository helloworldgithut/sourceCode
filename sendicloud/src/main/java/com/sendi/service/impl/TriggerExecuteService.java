package com.sendi.service.impl;

import com.sendi.dao.TriggerExecuteDaoI;
import com.sendi.entity.TriggerExecute;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TriggerExecuteService {
    @Autowired
    TriggerExecuteDaoI triggerExecuteDaoI;
    public TriggerExecute queryByTrigId(Integer trigId){
        TriggerExecute triggerExecute = triggerExecuteDaoI.queryByTrigId(trigId);
        return triggerExecute;
    }
}
