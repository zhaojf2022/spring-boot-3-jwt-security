package com.alibou.security.user;

import com.alibou.security.token.Token;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.Collection;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * 用户表，实现 UserDetails接口
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "_user")
public class User implements UserDetails {

  @Id
  @GeneratedValue
  private Integer id;
  private String firstname;
  private String lastname;
  private String email;
  private String password;

  // 用户的角色（枚举值）
  @Enumerated(EnumType.STRING)
  private Role role;

  // 一个用户（User）对象中，拥有多个令牌（Token）对象。
  @OneToMany(mappedBy = "user")
  private List<Token> tokens;

  // 获取角色对应的权限列表
  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return role.getAuthorities();
  }

  @Override
  public String getPassword() {
    return password;
  }

  // 获取用户名（使用 email 作为用户名）
  @Override
  public String getUsername() {
    return email;
  }

  // 用户账号是否过期
  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  // 用户账号是否没有被封禁
  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  // 用户凭证是否没有过期
  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  // 用户是否启用
  @Override
  public boolean isEnabled() {
    return true;
  }
}
