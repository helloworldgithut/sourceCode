package com.sendi.dao;

import com.sendi.entity.Trigger;
import org.apache.ibatis.annotations.Param;

import java.math.BigInteger;
import java.util.List;

public interface TriggerDaoI {
    public List<Integer> query(@Param("devId") BigInteger devId, @Param("resId") Integer resId);

    List<Trigger> queryAll();

    public int addTrigger(Trigger trigger);

    int updateStateByDoExecute(Trigger trigger);

    Trigger queryById(Integer id);
}
