package com.joe.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.joe.domin.bo.SysRolePermission;
import com.joe.domin.vo.RolePermRule;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author Joe
 * @since 2019-06-20
 */
public interface SysRolePermissionMapper extends BaseMapper<SysRolePermission> {

    /* *
     * @Description
     * 加载基于角色/资源的过滤规则
     * 即：用户-角色-资源（URL），对应关系存储与数据库中
     * 在shiro中生成的过滤器链为：url=jwt[角色1、角色2、角色n]
     * @Param []
     * @Return java.util.List
     */
    public List<RolePermRule> loadRolePermRules();

}
