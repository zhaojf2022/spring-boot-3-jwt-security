package com.alibou.security.auditing;

import com.alibou.security.user.User;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

/**
 * 提供当前用户ID，用于审计目的。
 * 实现了Spring Data提供的AuditorAware接口。
 * 使用泛型Integer，因为用户（审计员）的ID是Integer类型。
 */
public class ApplicationAuditAware implements AuditorAware<Integer> {

    /**
     * 获取当前审计员的ID
     * @return Optional<Integer> - 认证用户的ID，如果没有认证用户，则返回空的Optional。
     */
    @Override
    public Optional<Integer> getCurrentAuditor() {

        // 从SecurityContextHolder获取当前的认证对象
        Authentication authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();

        //如果认证对象为null，未认证，或者是匿名认证令牌的实例，则返回一个空的Optional
        if (authentication == null ||
            !authentication.isAuthenticated() ||
            authentication instanceof AnonymousAuthenticationToken
        ) {
            return Optional.empty();
        }

        // 从认证对象中检索User对象（userPrincipal）
        User userPrincipal = (User) authentication.getPrincipal();

        // 返回包装在Optional中的用户ID
        return Optional.ofNullable(userPrincipal.getId());
    }
}