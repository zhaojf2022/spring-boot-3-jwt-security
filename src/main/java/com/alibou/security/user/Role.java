package com.alibou.security.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.alibou.security.user.Permission.*;

/**
 * 角色权限定义
 */
@RequiredArgsConstructor
public enum Role {

  /**
   * 定义三个角色：USER，MANAGER，ADMIN;
   * 每个角色包含一个权限的集合
   */
  USER(Collections.emptySet()),
  ADMIN(
      Set.of(
          ADMIN_READ,
          ADMIN_UPDATE,
          ADMIN_DELETE,
          ADMIN_CREATE,
          MANAGER_READ,
          MANAGER_UPDATE,
          MANAGER_DELETE,
          MANAGER_CREATE
      )
  ),
  MANAGER(
      Set.of(
          MANAGER_READ,
          MANAGER_UPDATE,
          MANAGER_DELETE,
          MANAGER_CREATE
      )
  );

  @Getter
  private final Set<Permission> permissions;

/**
 * 获取简单授权对象的列表
 */
public List<SimpleGrantedAuthority> getAuthorities() {

    // 从权限列表中获取权限，并将其转换为SimpleGrantedAuthority对象列表
    var authorities = getPermissions()
            .stream()
            .map(permission -> new SimpleGrantedAuthority(permission.getPermission()))
            .collect(Collectors.toList());

    // 将角色名称也添加到SimpleGrantedAuthority对象列表中
    authorities.add(new SimpleGrantedAuthority("ROLE_" + this.name()));

    return authorities;
  }
}
