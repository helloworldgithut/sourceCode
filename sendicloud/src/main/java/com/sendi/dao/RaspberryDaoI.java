package com.sendi.dao;

import com.sendi.entity.Raspberry;

import java.util.List;
/**
    * @Author Mengfeng Qin
    * @Description 树莓派mapper接口
    * @Date 2019/4/1 15:25
*/
public interface RaspberryDaoI {

    /**
     * 根据hash_result 查询对应的树莓派信息列表
     * @param hashResult
     * @return 树莓派信息列表
     */
    List<Raspberry> queryByHashResult(String hashResult);

    /**
     * 根据hash_result 查询对应的树莓派信息
     * @param hashResult
     * @return 树莓派信息
     */
    Raspberry queryInfoByHashResult(String hashResult);

    /**
     * 通过hash_result查询对应的SN码
     * @param hashResult
     * @return
     */
    String querySNByHashResult(String hashResult);

    /**
     *  通过树莓派SN码查询对应的hash_result
     * @param snCode
     * @return
     */
    String queryHashResultBySnCode(String snCode);
}
