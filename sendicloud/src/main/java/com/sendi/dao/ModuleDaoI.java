package com.sendi.dao;

import com.sendi.entity.Module;

import java.util.List;

public interface ModuleDaoI {
    public List<Module> queryByModId(String modId);

    public Module queryByMod(String modId);
}
