package com.sendi.dao;

import com.sendi.entity.Trigger;
import org.apache.ibatis.annotations.Param;

import java.math.BigInteger;
import java.util.List;

/***
    * @Author Mengfeng Qin
    * @Description
    * @Date 2019/4/1 16:45
*/
public interface TriggerDaoI {

    /**
     * 查询所有触发器
     * @return 触发器列表
     */
    List<Trigger> queryAll();

    /**
     * 通过触发器ID 更新触发器信息
     * @param trigger
     * @return
     */
    int updateStateByDoExecute(Trigger trigger);

    /**
     * 通过触发器ID查询触发器信息
     * @param id
     * @return
     */
    Trigger queryById(Integer id);
}
