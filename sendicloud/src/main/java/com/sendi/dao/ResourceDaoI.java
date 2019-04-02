package com.sendi.dao;

import com.sendi.entity.Resource;

import java.math.BigInteger;
import java.util.List;
/**
    * @Author Mengfeng Qin
    * @Description 资源mapper 接口
    * @Date 2019/4/1 16:03
*/
public interface ResourceDaoI {
    /**
     * 添加资源
     * @param resource
     */
    public void addResource(Resource resource);

    /**
     * 根据设备ID查询设备列表
     * @param devId
     * @return
     */
    public List<Resource> queryByDevID(BigInteger devId);

    /**
     * 通过设备ID和资源名称查询对应的资源信息
     * @param resource
     * @return
     */
    public Resource queryByDevIDAndName(Resource resource);

    /**
     * 根据设备ID和资源名称更新资源信息
     * @param resource
     * @return
     */
    int updateResource(Resource resource);

    /**
     * 通过设备ID 更新资源状态为离线0
     * @param devId
     */
    void updateOfflineByDevId(BigInteger devId);

}
