package com.alibou.security.config;

import com.alibou.security.service.JwtService;
import com.alibou.security.token.TokenRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Jwt 认证过滤器
 */

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtService jwtService;
  private final UserDetailsService userDetailsService;
  private final TokenRepository tokenRepository;

  /**
   * 过滤器。从请求中提取 jwt，如果token 有效，则从中提取认证信息保存到上下文中
   * @param request HttpServletRequest
   * @param response HttpServletResponse
   * @param filterChain FilterChain
   * @throws ServletException 服务异常
   * @throws IOException IO异常
   */
  @Override
  protected void doFilterInternal(
          @NonNull HttpServletRequest request,
          @NonNull HttpServletResponse response,
          @NonNull FilterChain filterChain
      ) throws ServletException, IOException {

    // 如果是认证请求，直接放行
    if (request.getServletPath().contains("/api/v1/auth")) {
      filterChain.doFilter(request, response);
      return;
    }

    // 获取认证头
    final String authHeader = request.getHeader("Authorization");
    final String jwt;
    final String userEmail;

    // 如果没有认证头，或者认证头不是以 Bearer 开头，直接放行
    if (authHeader == null ||!authHeader.startsWith("Bearer ")) {
      filterChain.doFilter(request, response);
      return;
    }

    // 提取 jwt（认证头中'Bearer '后面的部分）
    jwt = authHeader.substring(7);
    // 提取用户邮箱
    userEmail = jwtService.extractUsername(jwt);

    // 如果用户邮箱不为空，并且当前上下文中没有认证信息
    if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
      // 从用户详情服务中加载用户详情
      UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);
      // Token 是否有效的标志
      var isTokenValid = tokenRepository.findByToken(jwt)
          // 如果 Token 不为空，且 Token 未过期，且 Token 未撤销，则返回 true，否则返回 false
          .map(token -> !token.isExpired() && !token.isRevoked())
          .orElse(false);

      // 如果 Token 有效，则设置认证信息
      if (jwtService.isTokenValid(jwt, userDetails) && Boolean.TRUE.equals(isTokenValid)) {
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
            userDetails,
            null,
            userDetails.getAuthorities()
        );
        authToken.setDetails(
            new WebAuthenticationDetailsSource().buildDetails(request)
        );
        // 将 token认证信息存放在上下文中
        SecurityContextHolder.getContext().setAuthentication(authToken);
      }
    }
    // 放行
    filterChain.doFilter(request, response);
  }
}
