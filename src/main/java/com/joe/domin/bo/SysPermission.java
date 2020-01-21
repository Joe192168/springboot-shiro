package com.joe.domin.bo;

import lombok.Data;

/**
 * 资源实体
 */
@Data
public class SysPermission {

    private Long id;
    private Long parentId;
    private String resName;
    private String resType;
    private String permission;
    private String url;

}
