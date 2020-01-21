package com.joe.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.joe.domin.bo.SysPermission;
import com.joe.mapper.SysPermissionMapper;
import com.joe.service.SysPermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Joe
 * @since 2019-06-20
 */
@Service
public class SysPermissionServiceImpl extends ServiceImpl<SysPermissionMapper, SysPermission> implements SysPermissionService {

    @Autowired
    private SysPermissionMapper sysPermissionMapper;

    @Override
    public List<String> getRolePermissionByUserId(String userName) {
        return sysPermissionMapper.getRolePermissionByUserId(userName);
    }
}
