package com.sendi.dao;

import com.sendi.entity.TriggerOperation;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TriggerOperationDaoI {

    public List<TriggerOperation> queryByTrigId(@Param("trigId") Integer trigId);

    List<TriggerOperation> queryAll();

}
