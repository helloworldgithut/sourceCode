package com.sendi.service.impl;

import com.sendi.dao.ModuleDaoI;
import com.sendi.entity.Module;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ModuleServiceImpl {
    @Autowired
    private ModuleDaoI moduleDaoI;

    public int queryByModId(String modId){
        List<Module> list = moduleDaoI.queryByModId(modId);
        int n = list==null?0:list.size();
        return n;
    }

    public Module queryByMod(String modId){
        Module module = moduleDaoI.queryByMod(modId);
        return module;
    }
}
