package com.joe.ream;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.joe.domin.bo.SysUser;
import com.joe.domin.vo.JwtAccount;
import com.joe.service.SysPermissionService;
import com.joe.service.SysUserService;
import com.joe.util.JWTUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;

/**
 * 认证和鉴权接口
 */
@Log4j2
public class UserRealm extends AuthorizingRealm {

    //token密钥
    @Value("${security.secret}")
    private String secret;

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private SysPermissionService sysPermissionService;

    /**
     * 大坑！，必须重写此方法，不然Shiro会报错
     */
    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof JWTToken;
    }

    /**
     * 授权
     *
     * @param principals
     * @return
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        String token=principals.getPrimaryPrincipal().toString();
        // 解密获得userName，用于和数据库进行查询
        JwtAccount jwtAccount = JWTUtil.parseJWT(token,secret);
        //从数据库读取资源
        List<String> sysPermissions = sysPermissionService.getRolePermissionByUserId(jwtAccount.getUserId());
        //可以控制到GET POST请求
        //List<String> sysPermissions=new ArrayList<>();
        //sysPermissions.add("/GET/sysUser/**");
        //sysPermissions.add("/DELETE/sysUser");
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        info.addStringPermissions(sysPermissions);
        return info;
    }

    /**
     * 认证流程
     *
     * @param
     * @return
     * @throws AuthenticationException
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken auth) throws AuthenticationException {
        String token = (String) auth.getCredentials();
        //校验token的正确性
        JwtAccount jwtAccount = null;
        try{
            jwtAccount = JWTUtil.parseJWT(token,secret);
        } catch(SignatureException | UnsupportedJwtException | MalformedJwtException | IllegalArgumentException e){
            log.error("SignatureException令牌错误：",e);
            throw new AuthenticationException("errJwt"); // 令牌错误
        } catch(ExpiredJwtException e){
            log.error("ExpiredJwtException令牌过期：",e);
            throw new AuthenticationException("expiredJwt"); // 令牌过期
        } catch(Exception e){
            log.error("Exception令牌错误：",e);
            throw new AuthenticationException("errJwt");// 令牌错误
        }
        if(null == jwtAccount){
            throw new AuthenticationException("errJwt");// 令牌为空
        }
        // 解密获得userName，用于和数据库进行对比
        //查询用户是否存在
        Wrapper<SysUser> objectEntityWrapper = new EntityWrapper<>();
        objectEntityWrapper.eq("user_name",jwtAccount.getUserId());
        SysUser sysUser = sysUserService.selectOne(objectEntityWrapper);
        if (sysUser == null) {
            throw new AuthenticationException("User didn't existed!");
        }
        return new SimpleAuthenticationInfo(token, token, "my_realm");
    }

}
