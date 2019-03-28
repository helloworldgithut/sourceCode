package com.sendi.service.impl;

import com.sendi.dao.RaspberryDaoI;
import com.sendi.entity.Raspberry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RaspberryService {

    @Autowired
    RaspberryDaoI raspberryDao;
    public int queryByHashResult(String hashResult){
        List<Raspberry> datas = raspberryDao.queryByHashResult(hashResult);
        int n = datas==null?0:datas.size();
        return n;
    }

    public Raspberry queryInfoByHashResult(String hashResult){
        Raspberry raspberry = raspberryDao.queryInfoByHashResult(hashResult);
        return raspberry;
    }

    public String querySNByHashResult(String hashResult){
        String snCode = raspberryDao.querySNByHashResult(hashResult);
        return snCode;
    }
    public String queryHashResultBySnCode(String SnCode){
        String hashResult = raspberryDao.queryHashResultBySnCode(SnCode);
        return hashResult;
    }

}
