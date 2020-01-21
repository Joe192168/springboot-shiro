package com.joe.domin.vo;

import lombok.Data;

import java.util.Date;

/**
 * @program: springboot-shiro
 * @description: jwt实体
 * @author: Joe
 * @create: 2020-01-21 17:35
 **/
@Data
public class JwtAccount {

    private String tokenId;// 令牌id
    private String userId;// 客户标识（用户名、账号）
    private String issuer;// 签发者(JWT令牌此项有值)
    private Date issuedAt;// 签发时间
    private String audience;// 接收方(JWT令牌此项有值)
    private String roles;// 访问主张-角色(JWT令牌此项有值)
    private String perms;// 访问主张-资源(JWT令牌此项有值)
    private String host;// 客户地址

}
