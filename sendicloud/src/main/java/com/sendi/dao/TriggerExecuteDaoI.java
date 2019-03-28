package com.sendi.dao;

import com.sendi.entity.TriggerExecute;
import org.apache.ibatis.annotations.Param;

public interface TriggerExecuteDaoI {
    public TriggerExecute queryByTrigId(@Param("trigId") Integer trigId);

}
