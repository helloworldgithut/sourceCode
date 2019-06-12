package com.sendi.service.impl;

import com.sendi.dao.ImageDataDaoI;
import com.sendi.entity.ImageData;
import com.sendi.utils.Base64Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ImageDataService {
    @Autowired
    ImageDataDaoI imgDao;
    public void addData(ImageData imageData){
        imgDao.addData(imageData);
    }

    public List<Integer> queryByFlag(String flag){
        List<Integer> list = imgDao.queryByFlag(flag);
        return list;
    }

    public String queryImageByFlag(String flag){
        StringBuilder sb = new StringBuilder();
        List<String> list = imgDao.queryImageByFlag(flag);
        for(String str : list){
            sb.append(str);
        }
      boolean bl =  Base64Utils.Base64ToImage(sb.toString(), "C:/Users/Administrator/Desktop/test1.jpg");

        System.out.println(bl);
        return sb.toString();
    }


}
