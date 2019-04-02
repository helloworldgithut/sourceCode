package com.sendi.dao;


import com.sendi.entity.ImageData;

import java.util.List;

public interface ImageDataDaoI {
     void addData(ImageData data);

     List<Integer> queryByFlag(String flag);

     List<String> queryImageByFlag(String flag);
}
