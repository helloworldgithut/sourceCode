package com.sendi.entity;

import lombok.Data;

import java.util.Date;

@Data
public class Product {
    private Integer id;//产品编号
    private String proName;//产品名称
    private String proType;//产品行业（类型）
    private String protocol;//产品接入协议
    private String description;//产品描述
    private Date createTime;//创建时间
    private String APIKey;//接口秘钥
    private String operator;//运营商
    private String userId;//用户ID
}
