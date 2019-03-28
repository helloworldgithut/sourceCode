package com.sendi.dao;

import com.sendi.entity.Device;
import org.apache.ibatis.annotations.Param;

import java.math.BigInteger;
import java.util.List;

public interface DeviceDaoI {
//    public List<Device> queryBySnCode(Device device);

    public Device queryAll(Device device);

    public  List<Device> queryBySnCode(String snCode);

    public void  updateByProAndMod(Device device);

    public Device queryById(BigInteger devId);

    public void updateOnlineBySnCode(String snCode);

    public void updateOfflineBySnCode(String snCode);

    public void updateOnlineById(BigInteger id);

    public void updateOfflineById(BigInteger id);

    public void updateOfflineByProId(Integer proId);

    public void addDevice(Device device);

    public Device queryByProAndMod(@Param("proId") Integer proId, @Param("modId") String modId);

    public List<Device> queryByProId(Integer proId);

    public void updateById(Device device);
}
