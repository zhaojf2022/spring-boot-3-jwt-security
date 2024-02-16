package com.alibou.security.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;

import static com.alibou.security.user.Permission.ADMIN_CREATE;
import static com.alibou.security.user.Permission.ADMIN_DELETE;
import static com.alibou.security.user.Permission.ADMIN_READ;
import static com.alibou.security.user.Permission.ADMIN_UPDATE;
import static com.alibou.security.user.Permission.MANAGER_CREATE;
import static com.alibou.security.user.Permission.MANAGER_DELETE;
import static com.alibou.security.user.Permission.MANAGER_READ;
import static com.alibou.security.user.Permission.MANAGER_UPDATE;
import static com.alibou.security.user.Role.ADMIN;
import static com.alibou.security.user.Role.MANAGER;
import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpMethod.PUT;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

/**
 * 安全配置
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfiguration {

    private static final String API_PATTERN = "/api/v1/management/**";

    // 接口白名单列表
    private static final String[] WHITE_LIST_URL = {
        "/api/v1/auth/**",
        "/v2/api-docs",
        "/v3/api-docs",
        "/v3/api-docs/**",
        "/swagger-resources",
        "/swagger-resources/**",
        "/configuration/ui",
        "/configuration/security",
        "/swagger-ui/**",
        "/webjars/**",
        "/swagger-ui.html"};

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;
    private final LogoutHandler logoutHandler;

    /**
     * 配置应用程序的安全设置的安全过滤链
     * @param http HttpSecurity Spring Security提供的一个构建器，用于自定义安全特性
     * @return SecurityFilterChain
     * @throws Exception 异常
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // 禁用CSRF（跨站请求伪造）保护。这通常用于API，因为它们是无状态的，不使用cookies
            .csrf(AbstractHttpConfigurer::disable)
            // 配置应用程序的访问控制
            .authorizeHttpRequests(req ->
                // 所有白名单的URL都允许访问
                req.requestMatchers(WHITE_LIST_URL)
                    .permitAll()
                    // 定义"/api/v1/management/**"接口的访问权限
                    .requestMatchers(API_PATTERN).hasAnyRole(ADMIN.name(), MANAGER.name())
                    .requestMatchers(GET, API_PATTERN).hasAnyAuthority(ADMIN_READ.name(), MANAGER_READ.name())
                    .requestMatchers(POST, API_PATTERN).hasAnyAuthority(ADMIN_CREATE.name(), MANAGER_CREATE.name())
                    .requestMatchers(PUT, API_PATTERN).hasAnyAuthority(ADMIN_UPDATE.name(), MANAGER_UPDATE.name())
                    .requestMatchers(DELETE, API_PATTERN).hasAnyAuthority(ADMIN_DELETE.name(), MANAGER_DELETE.name())
                    .anyRequest()
                    .authenticated()
            )
            // 配置会话管理。设置为无状态（STATELESS），即服务器不会创建或使用任何会话
            .sessionManagement(session -> session.sessionCreationPolicy(STATELESS))
            // 配置身份验证提供者
            .authenticationProvider(authenticationProvider)
            // 在用户密码验证过滤器之前，添加一个自定义的 jwt 认证过滤器
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
            // 配置登出
            .logout(logout ->
                logout.logoutUrl("/api/v1/auth/logout")
                    // 设置登出处理器（LogoutHandler），用于处理登出逻辑
                    .addLogoutHandler(logoutHandler)
                    // 设置成功登出处理器（LogoutSuccessHandler），用于清除安全上下文（SecurityContext）。
                    .logoutSuccessHandler((request, response, authentication) -> SecurityContextHolder.clearContext())
        );

        return http.build();
    }
}
