package com.joe.util;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * @author Joe
 * @date 2019/7/2 20:54
 * @description: 接口.
 */
@Data
@Configuration
public class JwtProperties {

    /**
     * 秘钥
     **/
    @Value("${security.secret}")
    private String secret;
    /**
     * 过期时间
     **/
    @Value("${token.tokenExpireTime}")
    private Integer tokenExpireTime;

}
