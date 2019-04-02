package com.sendi.service.impl;

import com.sendi.dao.RaspberryUserMergeDaoI;
import com.sendi.entity.RaspberryUserMerge;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RaspberryUserMergeService {
    @Autowired
    private RaspberryUserMergeDaoI raspberryUserMergeDaoI;

    public void addRaspberryUserMerge(RaspberryUserMerge raspberryUserMerge){
       raspberryUserMergeDaoI.addRaspberryUserMerge(raspberryUserMerge);
    }

    public void deleteBySnCode(String snCode){
        raspberryUserMergeDaoI.deleteBySnCode(snCode);
    }

    public int queryNumBySnCode(String snCode){
        List<RaspberryUserMerge> list = raspberryUserMergeDaoI.queryNumBySnCode(snCode);
        int n = list==null?0:list.size();
        return n;
    }

    public RaspberryUserMerge queryBySnCode(String snCode){
        RaspberryUserMerge raspberryUserMerge = raspberryUserMergeDaoI.queryBySnCode(snCode);
        return raspberryUserMerge;
    }
}
