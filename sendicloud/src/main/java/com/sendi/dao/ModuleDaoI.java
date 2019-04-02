package com.sendi.dao;

import com.sendi.entity.Module;

import java.util.List;
/***
    * @Author Mengfeng Qin
    * @Description 模块mapper接口
    * @Date 2019/3/29 15:19
*/
public interface ModuleDaoI {
    /**
     *根据模块ID查询模块的所有信息
     * @param modId
     * @return
     */
     List<Module> queryByModId(String modId);

    /**
     *  根据模块ID查询模块的所有信息
     * @param modId
     * @return
     */
     Module queryByMod(String modId);
}
