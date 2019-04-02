package com.sendi.service.impl;

import com.sendi.dao.TriggerOperationDaoI;
import com.sendi.entity.TriggerOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TriggerOperationService {
    @Autowired
    TriggerOperationDaoI triggerOperationDaoI;
    public List<TriggerOperation> queryByTrigId(Integer trigId){
        List<TriggerOperation> triggerOperation = triggerOperationDaoI.queryByTrigId(trigId);
        return triggerOperation;
    }

}
