package com.sendi.service.impl;

import com.sendi.dao.NumberDataDaoI;
import com.sendi.entity.NumberData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NumberDataService {
    @Autowired
    NumberDataDaoI ndsi;

    public void addData(NumberData data){
        ndsi.addData(data);
    }

}
