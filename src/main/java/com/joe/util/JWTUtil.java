package com.joe.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.SignatureGenerationException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.extern.log4j.Log4j2;
import org.apache.shiro.authc.AuthenticationException;

import java.io.UnsupportedEncodingException;
import java.util.Date;

/**
 * @author Joe
 * @date 2019/6/24 22:27
 * @description: JWTUtil工具类.
 */
@Log4j2
public class JWTUtil {

    // 过期时间5分钟
    private static final long EXPIRE_TIME = 1*60*1000;

    /**
     * 校验token是否正确
     * @param token 密钥
     * @param secret 用户的密码
     * @return 是否正确
     */
    public static boolean verify(String token, String userName, String secret) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            JWTVerifier verifier = JWT.require(algorithm)
                    .withClaim("userName", userName)
                    .build();
            DecodedJWT jwt = verifier.verify(token);
            log.info("验证jwt：[{}]",jwt);
        }catch (SignatureVerificationException | SignatureGenerationException e){
            log.info("jwt签名生成或验证异常：[{}]",e);
            throw new AuthenticationException("errJwt");
        }catch (TokenExpiredException e){
            log.info("jwt令牌过期异常：[{}]",e);
            throw new AuthenticationException("expiredJwt");
        }catch (Exception e) {
            log.info("jwt验证异常：[{}]",e);
            throw new AuthenticationException("errJwt");
        }
        return true;
    }

    /**
     * 获得token中的信息无需secret解密也能获得
     * @return token中包含的用户名
     */
    public static String getUserName(String token) {
        try {
            DecodedJWT jwt = JWT.decode(token);
            return jwt.getClaim("userName").asString();
        } catch (JWTDecodeException e) {
            return null;
        }
    }

    /**
     * 生成签名,5min后过期
     * @param userId id
     * @param secret 用户的密码
     * @return 加密的token
     */
    public static String sign(String userId, String secret) {
        try {
            Date date = new Date(System.currentTimeMillis()+EXPIRE_TIME);
            Algorithm algorithm = Algorithm.HMAC256(secret);
            // 附带userId信息
            return JWT.create()
                    .withClaim("userName", userId)
                    .withExpiresAt(date)
                    .sign(algorithm);
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }

    public static String getClaim(String token, String exp) {
        try {
            DecodedJWT jwt = JWT.decode(token);
            return jwt.getClaim(exp).asString();
        } catch (JWTDecodeException e) {
            return null;
        }
    }
}
