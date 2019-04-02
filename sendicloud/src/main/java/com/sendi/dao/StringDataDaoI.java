package com.sendi.dao;

import com.sendi.entity.StringData;
/***
    * @Author Mengfeng Qin
    * @Description 字符串数据 mapper 接口
    * @Date 2019/4/1 16:28
*/
public interface StringDataDaoI {
    /**
     * 添加字符串数据到表string_data
     * @param data
     */
    public void addData(StringData data);
}
