package com.joe.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.joe.domin.bo.SysUser;
import com.joe.mapper.SysUserMapper;
import com.joe.service.SysUserService;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Joe
 * @since 2019-06-20
 */
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

}
