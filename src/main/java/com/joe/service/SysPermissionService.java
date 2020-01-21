package com.joe.service;

import com.baomidou.mybatisplus.service.IService;
import com.joe.domin.bo.SysPermission;
import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author Joe
 * @since 2019-06-20
 */
public interface SysPermissionService extends IService<SysPermission> {

    public List<String> getRolePermissionByUserId(String user_id);

}
