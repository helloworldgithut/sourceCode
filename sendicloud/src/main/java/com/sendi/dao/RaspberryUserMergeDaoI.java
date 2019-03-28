package com.sendi.dao;

import com.sendi.entity.RaspberryUserMerge;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface RaspberryUserMergeDaoI {
    public void  addRaspberryUserMerge(RaspberryUserMerge raspberryUserMerge);

    public void deleteRaspberryUserMerge(@Param("snCode") String snCode, @Param("userId") String userId);

    public void deleteBySnCode(String snCode);

    public List<RaspberryUserMerge> queryBySnCodeAndUserId(@Param("snCode") String snCode, @Param("userId") String userId);

    public List<RaspberryUserMerge> queryNumBySnCode(String snCode);

    public RaspberryUserMerge queryBySnCode(String snCome);

}
