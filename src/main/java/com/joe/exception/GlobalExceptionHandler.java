package com.joe.exception;

import com.joe.domin.vo.Message;
import org.apache.shiro.authz.UnauthenticatedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.http.HttpServletRequest;

/**
 * 全局异常捕获
 */
@ControllerAdvice
public class GlobalExceptionHandler implements ApplicationContextAware {

    private Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private ApplicationContext applicationContext;

    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public Message defaultErrorHandler(HttpServletRequest request, Exception e) throws Exception {
        if(e instanceof UnauthenticatedException){
            return new Message().error("无认证");
        }
        return new Message().error(e.getMessage());
    }

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}
}
