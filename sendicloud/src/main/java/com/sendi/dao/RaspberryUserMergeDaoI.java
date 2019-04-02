package com.sendi.dao;

import com.sendi.entity.RaspberryUserMerge;
import org.apache.ibatis.annotations.Param;

import java.util.List;
/***
    * @Author Mengfeng Qin
    * @Description 树莓派与用户关联mapper接口
    * @Date 2019/4/1 15:36
*/
public interface RaspberryUserMergeDaoI {
    /**
     * 添加树莓派与用户的关联
     * @param raspberryUserMerge
     */
    public void  addRaspberryUserMerge(RaspberryUserMerge raspberryUserMerge);


//    public void deleteRaspberryUserMerge(@Param("snCode") String snCode, @Param("userId") String userId);
//    <delete id="deleteRaspberryUserMerge" >
//    delete from raspberry_user_merge
//    where sn_code = #{snCode} and user_id = #{userId}
//    </delete>

    /**
     * 根据树莓派的SN码删除与用户的关联
     * @param snCode
     */
    public void deleteBySnCode(String snCode);

//    public List<RaspberryUserMerge> queryBySnCodeAndUserId(@Param("snCode") String snCode, @Param("userId") String userId);
//    <select id="queryBySnCodeAndUserId" resultType="com.sendi.entity.RaspberryUserMerge">
//    select * from raspberry_user_merge
//    where sn_code = #{snCode} and user_id = #{userId}
//    </select>

    /**
     * 根据树莓派SN码查询对应的关系数量
     * @param snCode
     * @return 树莓派与用户的关联列表
     */
    public List<RaspberryUserMerge> queryNumBySnCode(String snCode);

    /**
     * 根据树莓派SN码查询对应的
     * @param snCode
     * @return 树莓派与用户的关联
     */
    public RaspberryUserMerge queryBySnCode(String snCode);

}
