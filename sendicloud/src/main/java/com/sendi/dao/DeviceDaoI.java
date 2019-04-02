package com.sendi.dao;

import com.sendi.entity.Device;
import org.apache.ibatis.annotations.Param;

import java.math.BigInteger;
import java.util.List;
/**
    * @Author Mengfeng Qin
    * @Description 设备mapper 接口
    * @Date 2019/3/29 14:03
*/
public interface DeviceDaoI {
    /**
     *  通过SN码查询关联的设备的所有信息
     * @param snCode
     * @return
     */
    List<Device> queryBySnCode(String snCode);
    /**
     * 通过pro_id和mod_id码更新IP 、PORT 、SN码
     * @param device
     * @return
     */
    void  updateByProAndMod(Device device);

    /**
     * 根据设备ID查询设备的所有信息
     * @param devId
     * @return
     */
    Device queryById(BigInteger devId);

    /**
     *  根据SN码更新关联的设备全部下线
     * @param snCode
     */
    void updateOfflineBySnCode(String snCode);

    /**
     * 通过设备ID更新该设备状态为1（在线）
     * @param id
     */
    void updateOnlineById(BigInteger id);

    /**
     * 通过设备ID更新该设备状态为0（离线）
     * @param id
     */
    void updateOfflineById(BigInteger id);

    /**
     * 根据产品ID更新该产品下的所有设备状态为0（离线）
     * @param proId
     */
    void updateOfflineByProId(Integer proId);

    /**
     *  添加设备
     * @param device
     */
    void addDevice(Device device);

    /**
     *  根据pro_id 和 mod_id 查询对应的设备信息
     * @param proId
     * @param modId
     * @return
     */
    Device queryByProAndMod(@Param("proId") Integer proId, @Param("modId") String modId);

    /**
     *  通过产ID，查询该产品下的所有设备信息
     * @param proId
     * @return
     */
    List<Device> queryByProId(Integer proId);

    /**
     *  根据设备ID更新设备的相关信息
     * @param device
     */
    void updateById(Device device);
}
