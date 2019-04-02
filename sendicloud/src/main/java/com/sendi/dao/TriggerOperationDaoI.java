package com.sendi.dao;

import com.sendi.entity.TriggerOperation;
import org.apache.ibatis.annotations.Param;

import java.util.List;
/**
    * @Author Mengfeng Qin
    * @Description 触发器触发条件mapper接口
    * @Date 2019/4/1 18:14
*/
public interface TriggerOperationDaoI {
    /**
     * 通过触发器ID查询对应的触发条件
     * @param trigId
     * @return
     */
    public List<TriggerOperation> queryByTrigId(@Param("trigId") Integer trigId);

}
