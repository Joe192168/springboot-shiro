package com.joe.controller;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.joe.domin.bo.SysRole;
import com.joe.domin.bo.SysUser;
import com.joe.domin.bo.SysUserRole;
import com.joe.domin.vo.Message;
import com.joe.service.SysRoleService;
import com.joe.service.SysUserRoleService;
import com.joe.service.SysUserService;
import com.joe.util.JWTUtil;
import org.apache.shiro.authz.UnauthorizedException;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 登陆控制器
 */
@RestController
public class LonginController {

    @Value("${security.secret}")
    private String secret;
    @Value("${token.tokenExpireTime}")
    private Integer tokenExpireTime;

    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private SysUserRoleService sysUserRoleService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @PostMapping("/login")
    public Message login(@RequestBody SysUser sysUserReq) {
        try {
            Wrapper<SysUser> sysUserEntityWrapper = new EntityWrapper<>();
            sysUserEntityWrapper.eq("user_name",sysUserReq.getUserName());
            SysUser sysUserDB = sysUserService.selectOne(sysUserEntityWrapper);

            String roles = "1,2";
            //加密密码
            SimpleHash simpleHash = new SimpleHash("md5", sysUserReq.getPassword().getBytes(), sysUserDB.getSalt(), 1);
            if(sysUserDB.getPassword().equals(simpleHash.toHex())){
                String token = JWTUtil.createJWT("Joe",sysUserDB.getUserName(),sysUserDB.getUserName(),roles,"", secret);
                redisTemplate.opsForValue().set("JWT-SESSION-"+sysUserReq.getUserName(),token);
                redisTemplate.expire("JWT-SESSION-"+sysUserReq.getUserName(),2*tokenExpireTime, TimeUnit.MINUTES);
                return new Message().ok("登陆成功！").addData("token",token);
            }else{
                return new Message().error("用户名或密码错误！");
            }
        } catch (Exception e) {
            return new Message().error("登陆异常！");
        }
    }
}
