package com.sendi.dao;

import com.sendi.entity.Product;

import java.util.List;

public interface ProductDaoI {

    public List<Product> productList(Integer id);

    public Product queryByProId(Integer id);


}
