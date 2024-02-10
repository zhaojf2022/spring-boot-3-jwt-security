package com.alibou.security.service;

import com.alibou.security.token.TokenRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

/**
 * 登出服务
 */
@Service
@RequiredArgsConstructor
public class LogoutService implements LogoutHandler {

  private final TokenRepository tokenRepository;

  /**
   * 登出
   * @param request HttpServletRequest
   * @param response HttpServletResponse
   * @param authentication Authentication
   */
  @Override
  public void logout(
          HttpServletRequest request,
          HttpServletResponse response,
          Authentication authentication) {

    // 从请求头中提取 jwt
    final String authHeader = request.getHeader("Authorization");
    final String jwt;
    if (authHeader == null ||!authHeader.startsWith("Bearer ")) {
      return;
    }
    jwt = authHeader.substring(7);

    // 从数据库中查找 token
    var storedToken = tokenRepository.findByToken(jwt)
        .orElse(null);

    // 如果 token 存在，将其设置为过期和撤销状态
    if (storedToken != null) {
      storedToken.setExpired(true);
      storedToken.setRevoked(true);
      tokenRepository.save(storedToken);
      // 清除上下文
      SecurityContextHolder.clearContext();
    }
  }
}
