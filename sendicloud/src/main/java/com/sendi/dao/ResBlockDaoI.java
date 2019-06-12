package com.sendi.dao;

import com.sendi.entity.ResBlock;
import com.sendi.entity.Resource;

import java.math.BigInteger;
import java.util.List;

/***
    * @Author Mengfeng Qin
    * @Description
    * @Date 2019/4/3 11:39
*/
public interface ResBlockDaoI {

    void addResBlock(ResBlock resBlock);

    void deleteByToken(String token);

    List<ResBlock> queryByToken(String token);
}
