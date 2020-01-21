package com.joe.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.joe.domin.bo.SysPermission;
import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author Joe
 * @since 2019-06-20
 */
public interface SysPermissionMapper extends BaseMapper<SysPermission> {

    public List<String> getRolePermissionByUserId(String user_id);

}
