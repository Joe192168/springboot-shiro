package com.joe.domin.bo;

import lombok.Data;

/**
 * 用户实体
 */
@Data
public class SysUser {

    private Long userId;
    private String userName;
    private String fullName;
    private String password;
    private String salt;

}
