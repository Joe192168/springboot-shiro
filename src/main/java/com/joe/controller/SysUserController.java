package com.joe.controller;

import com.joe.domin.bo.SysUser;
import com.joe.domin.vo.Message;
import com.joe.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 用户控制器
 */
@RestController
@RequestMapping("/sysUser")
public class SysUserController {
    @Autowired
    private SysUserService sysUserService;

    @GetMapping("/getUserlist")
    public Message getUserList(){
        return new Message().ok("成功");
    }

    @GetMapping("/getList")
    public Message getList(){
        List<SysUser> sysUsers = sysUserService.selectList(null);
        return new Message().ok("获取数据成功!").addData("userList",sysUsers);
    }

}

