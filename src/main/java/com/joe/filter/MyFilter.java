package com.joe.filter;

import org.apache.shiro.web.filter.authc.BasicHttpAuthenticationFilter;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

/**
 * @program: springboot-shiro
 * @description: 自定义shiro过滤器
 * @author: Joe
 * @create: 2020-01-22 17:08
 **/
public class MyFilter extends BasicHttpAuthenticationFilter {

    @Override
    public boolean onPreHandle(ServletRequest request, ServletResponse response, Object mappedValue) throws Exception {
        return this.isAccessAllowed(request, response, mappedValue) || this.onAccessDenied(request, response, mappedValue);
    }

}
