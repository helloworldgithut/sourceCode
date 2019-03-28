package com.sendi.service.impl;

import com.sendi.dao.StringDataDaoI;
import com.sendi.entity.StringData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StringDataService {
    @Autowired
    StringDataDaoI strDao;
    public void addData(StringData stringData){
        strDao.addData(stringData);
    }
}
