package com.sendi.service.impl;

import com.sendi.dao.ProductDaoI;
import com.sendi.entity.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductServiceImpl  {

    @Autowired
    ProductDaoI pdi;

    public int productList(Integer id) {
        List<Product> list = pdi.productList(id);
        int n = list==null?0:list.size();
        return n;
    }

    public Product queryByProId(Integer id){
        Product product = pdi.queryByProId(id);
        return product;
    }

}
