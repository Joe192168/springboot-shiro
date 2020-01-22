package com.joe.util;

import com.joe.domin.vo.JwtAccount;
import io.jsonwebtoken.*;
import lombok.extern.log4j.Log4j2;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import javax.xml.bind.DatatypeConverter;
import java.util.*;

/**
 * @author Joe
 * @date 2019/6/24 22:27
 * @description: JWTUtil工具类.
 */
@Log4j2
public class JWTUtil {

    // jti：jwt的唯一身份标识
    public static final String JWT_ID = UUID.randomUUID().toString();

    // 过期时间，单位毫秒
    public static final int EXPIRE_TIME = 1 * 60 * 1000; // 一个小时

    /**
     * 签发jwt
     * @param issuer 签发人
     * @param audience 接收者
     * @param subject 用户ID
     * @param roles 访问主张-角色
     * @param permissions 访问主张-权限
     * @throws Exception
     */
    public static String createJWT(String issuer, String audience, String subject,String roles,String permissions,String jwt_secret) {
        JwtBuilder builder = null; // 设置签名，使用的是签名算法和签名使用的秘钥
        try {
            // 生成签名的时候使用的秘钥secret，切记这个秘钥不能外露，是你服务端的私钥，在任何场景都不应该流露出去，一旦客户端得知这个secret，那就意味着客户端是可以自我签发jwt的
            byte[] secreKeyBytes = DatatypeConverter.parseBase64Binary(jwt_secret);
            SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
            // 创建payload的私有声明（根据特定的业务需要添加，如果要拿这个做验证，一般是需要和jwt的接收方提前沟通好验证的方式）
            Map<String, Object> claims = new HashMap<>();
            claims.put("userName", subject);
            //claims.put("password", "123");
            // 生成JWT的时间
            long nowTime = System.currentTimeMillis();
            Date issuedAt = new Date(nowTime);
            // 为payload添加各种标准声明和私有声明
            builder = Jwts.builder() // 表示new一个JwtBuilder，设置jwt的body
    //				.setHeader(header) // 设置头部信息
                    .setClaims(claims) // 如果有私有声明，一定要先设置自己创建的这个私有声明，这是给builder的claim赋值，一旦写在标准的声明赋值之后，就是覆盖了那些标准的声明
                    .setId(JWT_ID) // jti(JWT ID)：jwt的唯一身份标识，根据业务需要，可以设置为一个不重复的值，主要用来作为一次性token，从而回避重放攻击
                    .setIssuedAt(issuedAt) // iat(issuedAt)：jwt的签发时间
                    .setIssuer(issuer) // iss(issuer)：jwt签发者
                    .setSubject(subject) // sub(subject)：jwt所面向的用户，放登录的用户名，一个json格式的字符串，可存放userid，roldid之类，作为用户的唯一标志
                    .signWith(signatureAlgorithm, secreKeyBytes);
            // 设置过期时间
            long expTime = EXPIRE_TIME;
            if (expTime >= 0) {
                long exp = nowTime + expTime;
                builder.setExpiration(new Date(exp));
            }
            // 设置jwt接收者
            if (!StringUtils.isEmpty(audience)) {
                builder.setAudience(audience);
            }
            if (!StringUtils.isEmpty(roles)) {
                builder.claim("roles",roles);
            }
            if (!StringUtils.isEmpty(permissions)) {
                builder.claim("perms",permissions);
            }
        } catch (Exception e) {
            log.error("签发jwt异常：[{}]",e);
        }
        return builder.compact();
    }

    /**
     * 解密jwt
     * @param jwt
     * @param jwt_secret
     * @return
     */
    public static JwtAccount parseJWT(String jwt, String jwt_secret) {
        JwtAccount jwtAccount = null;
        Claims claims = Jwts.parser()
                .setSigningKey(DatatypeConverter.parseBase64Binary(jwt_secret))
                .parseClaimsJws(jwt)
                .getBody();
        jwtAccount = new JwtAccount();
        jwtAccount.setTokenId(claims.getId());// 令牌ID
        jwtAccount.setUserId(claims.getSubject());// 客户标识
        jwtAccount.setIssuer(claims.getIssuer());// 签发者
        jwtAccount.setIssuedAt(claims.getIssuedAt());// 签发时间
        jwtAccount.setAudience(claims.getAudience());// 接收方
        jwtAccount.setRoles(claims.get("roles", String.class));// 访问主张-角色
        jwtAccount.setPerms(claims.get("perms", String.class));// 访问主张-权限
        return jwtAccount;
    }

    /**
     * 分割字符串进SET
     */
    public static Set<String> split(String str) {
        Set<String> set = new HashSet<>();
        if (StringUtils.isEmpty(str))
            return set;
        set.addAll(CollectionUtils.arrayToList(str.split(",")));
        return set;
    }

}
