package com.joe.filter;

import com.joe.domin.vo.RolePermRule;
import com.joe.service.SysRolePermissionService;
import com.joe.service.SysUserService;
import com.joe.util.JwtProperties;
import lombok.extern.log4j.Log4j2;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.filter.mgt.DefaultFilterChainManager;
import org.apache.shiro.web.servlet.AbstractShiroFilter;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import javax.servlet.Filter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @program: springboot-shiro
 * @description: Shiro配置器
 * @author: Joe
 * @create: 2020-01-22 10:01
 **/
@Log4j2
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ShiroFilterChainManager {

    private final SysRolePermissionService sysRolePermissionService;
    private final StringRedisTemplate redisTemplate;
    private final SysUserService sysUserService;

    public ShiroFilterChainManager(SysRolePermissionService sysRolePermissionService, StringRedisTemplate redisTemplate, SysUserService sysUserService) {
        this.sysRolePermissionService = sysRolePermissionService;
        this.redisTemplate = redisTemplate;
        this.sysUserService = sysUserService;
    }

    //初始化获取过滤链
    public Map<String, Filter> initGetFilters(JwtProperties jwtProp) {
        Map<String,Filter> filters = new LinkedHashMap<>();
        JWTFilter jwtFilter = new JWTFilter();
        jwtFilter.setRedisTemplate(redisTemplate);
        filters.put("jwt", jwtFilter);
        return filters;
    }

    // 初始化获取过滤链规则
    public Map<String,String> initGetFilterChain() {
        Map<String,String> filterChain = new LinkedHashMap<>();
        /*
         * 自定义url规则
         * http://shiro.apache.org/web.html#urls-
         */
        // 所有请求通过我们自己的JWT Filter
        filterChain.put("/login", "anon");
        filterChain.put("/**", "jwt");
        // 访问401和404页面不通过我们的Filter
        filterChain.put("/401", "anon");
        // -------------dynamic 动态URL
        if (sysRolePermissionService != null) {
            List<RolePermRule> rolePermRules = this.sysRolePermissionService.loadRolePermRules();
            if (null != rolePermRules) {
                rolePermRules.forEach(rule -> {
                    StringBuilder Chain = rule.toFilterChain();
                    if (null != Chain) {
                        filterChain.putIfAbsent(rule.getUrl(),Chain.toString());
                    }
                });
            }
        }
        return filterChain;
    }

}
