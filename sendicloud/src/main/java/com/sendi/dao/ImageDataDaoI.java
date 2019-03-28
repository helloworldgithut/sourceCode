package com.sendi.dao;


import com.sendi.entity.ImageData;

import java.util.List;

public interface ImageDataDaoI {
    public void addData(ImageData data);

    public List<Integer> queryByFlag(String flag);
    public List<String> queryImageByFlag(String flag);
}
