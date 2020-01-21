package com.joe.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 用户角色控制器
 */
@Controller
@RequestMapping("/sysUserRole")
public class SysUserRoleController {

    @GetMapping("/test")
    public String test(@PathVariable String str){
        return str;
    }

}

