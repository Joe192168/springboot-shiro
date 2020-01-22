package com.joe.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.joe.domin.bo.SysRolePermission;
import com.joe.domin.vo.RolePermRule;
import com.joe.mapper.SysRolePermissionMapper;
import com.joe.service.SysRolePermissionService;
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
public class SysRolePermissionServiceImpl extends ServiceImpl<SysRolePermissionMapper, SysRolePermission> implements SysRolePermissionService {

    @Autowired
    private SysRolePermissionMapper sysRolePermissionMapper;

    @Override
    public List<RolePermRule> loadRolePermRules() {
        return sysRolePermissionMapper.loadRolePermRules();
    }
}
