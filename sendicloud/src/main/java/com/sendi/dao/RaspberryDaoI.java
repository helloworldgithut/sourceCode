package com.sendi.dao;

import com.sendi.entity.Raspberry;

import java.util.List;

public interface RaspberryDaoI {
    List<Raspberry> queryByHashResult(String hashResult);

    Raspberry queryInfoByHashResult(String hashResult);

    String querySNByHashResult(String hashResult);

    String queryHashResultBySnCode(String snCode);
}
