package com.sendi.service.impl;

import com.sendi.config.TriggerPool;
import com.sendi.dao.ResourceDaoI;
import com.sendi.entity.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.List;

@Service
public class ResourceService {
    private Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private ResourceDaoI rdi;

    public void addResource (Resource resource){
        try {
            rdi.addResource(resource);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

//    public  Resource queryByName(String name){
//        Resource res = rsi.queryByName(name);
//        return  res;
//    }

    public void  delResource (Long devId){
        try{
            rdi.delResource(devId);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public List<Resource> queryByDevID(BigInteger devId){
        List<Resource> list = rdi.queryByDevID(devId);
        return  list;
    }

    public Resource queryByDevIDAndName(Resource resource){
//        resource.setDevId(10000000002L);
//        resource.setResName("Hall");
        Resource res = rdi.queryByDevIDAndName(resource);
        return  res;
    }

    public int updateResource(Resource resource){
        return  rdi.updateResource(resource);
    }



    public void updateOrAddResource(Resource resource){
        logger.info("res 查询参数:"+resource);
        //查询devID 和resName 是否存
        Resource resMap = rdi.queryByDevIDAndName(resource);
        if (resMap==null){
            rdi.addResource(resource);
            resource.setState(1);
            rdi.updateResource(resource);
            logger.info("资源不存在,插入并更新:"+resource);
//            rdi.updateOfflineByDevId(resource.getId());
        }else {
            //注册上来后更新触发器执行为false
            if(TriggerPool.triggers.containsKey(resMap.getId())){
                TriggerPool.isExecute.put(resMap.getId(), false);
            }
            resource.setState(1);
            rdi.updateResource(resource);
            logger.info("资源存在,更新资源状态:"+resource);
        }
    }

    public void updateOfflineByDevId(BigInteger devId){
        rdi.updateOfflineByDevId(devId);
    }
}
