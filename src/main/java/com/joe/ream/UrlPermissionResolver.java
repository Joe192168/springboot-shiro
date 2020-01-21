package com.joe.ream;

import org.apache.shiro.authz.Permission;
import org.apache.shiro.authz.permission.PermissionResolver;

/**
 * Url授权接口实现
 */
public class UrlPermissionResolver implements PermissionResolver {

    @Override
    public Permission resolvePermission(String s) {
        return new UrlPermission(s);
    }

}
