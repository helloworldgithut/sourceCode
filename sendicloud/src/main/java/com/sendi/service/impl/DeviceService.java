package com.sendi.service.impl;

import com.sendi.dao.DeviceDaoI;
import com.sendi.entity.Device;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.List;

@Service
public class DeviceService {

    @Autowired
    private DeviceDaoI deviceDaoI;

    public int queryBySncode(String snCode){
        List<Device> list =deviceDaoI.queryBySnCode(snCode);
        int n = list==null?0:list.size();
        return n;
    }
//    public Device queryAll(Device device){
//        Device dev = deviceDaoI.query(device);
//        return  dev;
//    }
//    public Device queryBySnCode(String snCode){
//        Device dev = deviceDaoI.queryBySnCode(snCode);
//        return  dev;
//    }

    public void updateByProAndMod(Device device){
        deviceDaoI.updateByProAndMod(device);
    }

    public void updateOnlineBySnCode(String snCode){
        deviceDaoI.updateOnlineBySnCode(snCode);
    }
    public void updateOfflineBySnCode(String snCode){
        deviceDaoI.updateOfflineBySnCode(snCode);
    }

    public void updateOnlineById(BigInteger id){
        deviceDaoI.updateOnlineById(id);
    }
    public void updateOfflineById(BigInteger id){
        deviceDaoI.updateOfflineById(id);
    }

    public void updateOfflineByProId(Integer proId){
        deviceDaoI.updateOfflineByProId(proId);
    }
    public Device queryById(BigInteger id){
            Device device = deviceDaoI.queryById(id);
            return device;
    }

    public void addDevice(Device device){
        deviceDaoI.addDevice(device);
    }

    public Device queryByProAndMod(Integer proId, String modId){
        Device device = deviceDaoI.queryByProAndMod(proId, modId);
        return device;
    }

    public List<Device>  queryByProId(Integer proId){
        List<Device> list = deviceDaoI.queryByProId(proId);
//        int n = list==null?0:list.size();
        return list;
    }

    public void updateById(Device device){
       deviceDaoI.updateById(device);
    }

}
