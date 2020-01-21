package com.joe.controller;

import com.joe.domin.bo.SysUser;
import com.joe.domin.vo.ResultVO;
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
    public ResultVO getUserList(){
        return null;
    }

    @GetMapping("/getList")
    public ResultVO getList(){
        List<SysUser> sysUsers = sysUserService.selectList(null);
        return new ResultVO(sysUsers);
    }

}

