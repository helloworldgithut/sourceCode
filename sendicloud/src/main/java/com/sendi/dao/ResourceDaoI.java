package com.sendi.dao;

import com.sendi.entity.Resource;

import java.math.BigInteger;
import java.util.List;

public interface ResourceDaoI {
    public void addResource(Resource resource);

    public void delResource(Long devId);

    public List<Resource> queryByDevID(BigInteger devId);

    public Resource queryByDevIDAndName(Resource resource);

    int updateResource(Resource resource);

    void updateOfflineByDevId(BigInteger devId);

//    public Resource queryByName(String name);
}
