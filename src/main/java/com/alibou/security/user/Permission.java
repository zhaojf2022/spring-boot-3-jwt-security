package com.alibou.security.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 权限表
 */

@RequiredArgsConstructor
public enum Permission {

    /**
     * 定义权限枚举值
     */

    ADMIN_READ("admin:read"),
    ADMIN_UPDATE("admin:update"),
    ADMIN_CREATE("admin:create"),
    ADMIN_DELETE("admin:delete"),
    MANAGER_READ("management:read"),
    MANAGER_UPDATE("management:update"),
    MANAGER_CREATE("management:create"),
    MANAGER_DELETE("management:delete");

    // 每个枚举值只包含一个字符串；
    @Getter
    private final String permission;
}
