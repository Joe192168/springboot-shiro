package com.joe.filter;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.joe.domin.vo.JwtAccount;
import com.joe.domin.vo.Message;
import com.joe.domin.vo.ResultVO;
import com.joe.ream.JWTToken;
import com.joe.util.IpUtil;
import com.joe.util.JWTUtil;
import com.joe.util.RequestResponseUtil;
import lombok.extern.log4j.Log4j2;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.BasicHttpAuthenticationFilter;
import org.apache.shiro.web.util.WebUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMethod;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.TimeUnit;

/**
 * jwt实现shiro接口
 */
@Log4j2
public class JWTFilter extends BasicHttpAuthenticationFilter {

    private static final String STR_EXPIRED = "expiredJwt";

    private String secret;

    private Integer tokenExpireTime;

    private StringRedisTemplate stringRedisTemplate;

    public JWTFilter(String secret, Integer tokenExpireTime, StringRedisTemplate stringRedisTemplate) {
        this.secret = secret;
        this.tokenExpireTime = tokenExpireTime;
        this.stringRedisTemplate = stringRedisTemplate;
    }

    /**
     * 判断用户是否想要登入。
     * 检测header里面是否包含Authorization字段即可
     */
    @Override
    protected boolean isLoginAttempt(ServletRequest request, ServletResponse response) {
        HttpServletRequest req = (HttpServletRequest) request;
        String authorization = req.getHeader("Authorization");
        return authorization != null;
    }

    /**
     * 执行登录流程
     */
    @Override
    protected boolean executeLogin(ServletRequest request, ServletResponse response) throws Exception {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        String authorization = httpServletRequest.getHeader("Authorization");

        JWTToken token = new JWTToken(authorization);
        // 提交给realm进行登入，如果错误他会抛出异常并被捕获
        getSubject(request, response).login(token);
        // 如果没有抛出异常则代表登入成功，返回true
        return true;
    }

    /**
     * @Author Joe
     * @Description 是否允许访问 true允许访问。当方法返回false时才会执行isAccessAllowed方法
     * @Date 15:23 2019/6/27
     * @Param [request, response, mappedValue]
     * @return boolean
     **/
    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue){
        if (isLoginAttempt(request, response)) {
            //登录验证
            try {
                executeLogin(request, response);
            } catch (AuthenticationException e) {
                // 如果是JWT过期
                if (STR_EXPIRED.equals(e.getMessage())) {
                    String userName = WebUtils.toHttp(request).getHeader("appId");
                    String token = WebUtils.toHttp(request).getHeader("authorization");
                    String refreshJwt = stringRedisTemplate.opsForValue().get("JWT-SESSION-"+userName);
                    //处理token过期的问题，登入成功后将token存入redis,并将过期时间设置为token过期的2倍，
                    //当token过期后,判断redis中的token是否存在，存在就生成新的token给前端,否则token过期,重新登录
                    if (null != refreshJwt && refreshJwt.equals(token)) {
                        //重新生成token
                        String newToken = JWTUtil.createJWT("Joe",userName,userName,"","", secret);
                        stringRedisTemplate.opsForValue().set("JWT-SESSION-"+userName,newToken);
                        stringRedisTemplate.expire("JWT-SESSION-"+userName,2*tokenExpireTime, TimeUnit.MINUTES);
                        //删掉以前的token
                        stringRedisTemplate.delete(token);
                        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
                        httpServletResponse.setHeader("Authorization", newToken);
                        httpServletResponse.setHeader("Access-Control-Expose-Headers", "Authorization");
                        Message message = new Message().ok(500,"登录超时，请重新登录!").addData("jwt",newToken);
                        RequestResponseUtil.responseWrite(JSON.toJSONString(message),response);
                        return false;
                    }else{
                        Message message = new Message().error(500,"登录超时，请重新登录!");
                        RequestResponseUtil.responseWrite(JSON.toJSONString(message),response);
                        return false;
                    }
                }else{
                    Message message = new Message().error(500,"error jwt");
                    RequestResponseUtil.responseWrite(JSON.toJSONString(message),response);
                    return false;
                }
            }catch (Exception e){
                // 其他错误
                log.error(IpUtil.getIpFromRequest(WebUtils.toHttp(request))+"--JWT认证失败"+e.getMessage(),e);
                // 告知客户端JWT错误1005,需重新登录申请jwt
                Message message = new Message().error(500,"error jwt");
                RequestResponseUtil.responseWrite(JSON.toJSONString(message),response);
                return false;
            }
        }else{
            // 请求未携带jwt 判断为无效请求
            Message message = new Message().error(500,"token不能为空");
            RequestResponseUtil.responseWrite(JSON.toJSONString(message),response);
            return false;
        }
        //url鉴权
        Subject subject = getSubject(request,response);
        //获取请求方式
        String method = WebUtils.toHttp(request).getMethod();
        //subject.isPermitted返回false表示未授权 true已授权 会去调用ream中的授权 这里控制的方法级别 /GET/sysUser/**
        //如果访问的url没有被授权则会拒绝访问，走访问拒绝的处理逻辑onAccessDenied，有则放行
        return subject.isPermitted("/"+method+getPathWithinApplication(request));
    }

    /**
     * onAccessDenied：当 isAccessAllowed 返回 false 的时候，才会执行 method onAccessDenied

     * @param servletRequest
     * @param servletResponse
     * @return
     * @throws Exception
     */
    @Override
    protected boolean onAccessDenied(ServletRequest servletRequest, ServletResponse servletResponse) throws IOException {
        Subject subject = getSubject(servletRequest,servletResponse);
        // 未认证的情况上面已经处理  这里处理未授权
        if (subject != null && subject.isAuthenticated()){
            //  已经认证但未授权的情况
            // 告知客户端JWT没有权限访问此资源
            Message message = new Message().error(403,"无访问此资源权限！");
            RequestResponseUtil.responseWrite(JSON.toJSONString(message),servletResponse);
        }
        // 过滤链终止
        return false;
    }

    /**
     * 对跨域提供支持
     */
    @Override
    protected boolean preHandle(ServletRequest request, ServletResponse response) throws Exception {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        httpServletResponse.setHeader("Access-control-Allow-Origin", httpServletRequest.getHeader("Origin"));
        httpServletResponse.setHeader("Access-Control-Allow-Methods", "GET,POST,OPTIONS,PUT,DELETE");
        httpServletResponse.setHeader("Access-Control-Allow-Headers", httpServletRequest.getHeader("Access-Control-Request-Headers"));
        // 跨域时会首先发送一个option请求，这里我们给option请求直接返回正常状态
        if (httpServletRequest.getMethod().equals(RequestMethod.OPTIONS.name())) {
            httpServletResponse.setStatus(HttpStatus.OK.value());
            return false;
        }
        return super.preHandle(request, response);
    }
}
