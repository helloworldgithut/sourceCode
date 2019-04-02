package com.sendi.dao;

import com.sendi.entity.Product;

import java.util.List;
/**
    * @Author Mengfeng Qin
    * @Description 产品mapper接口
    * @Date 2019/4/1 14:14
*/
public interface ProductDaoI {
    /**
     * 根据产品ID查询产品列表
     * @param id
     * @return 产品列表
     */
    public List<Product> productList(Integer id);

    /**
     * 根据产品ID查询产品
     * @param id
     * @return 产品
     */
    public Product queryByProId(Integer id);

}
