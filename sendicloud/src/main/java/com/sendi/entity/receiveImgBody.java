package com.sendi.entity;

import lombok.Data;

@Data
public class receiveImgBody {
    private String hash_result;
    private Integer exp_id;
    private Integer sort_id;
    private String mod_id;
    private String webtoken;
    private String type;
    private String flag;
    private String value;
    private Long time;


}
