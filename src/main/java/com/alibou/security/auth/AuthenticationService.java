package com.alibou.security.auth;

import com.alibou.security.service.JwtService;
import com.alibou.security.token.Token;
import com.alibou.security.token.TokenRepository;
import com.alibou.security.token.TokenType;
import com.alibou.security.user.User;
import com.alibou.security.user.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * 认证服务
 */
@Service
@RequiredArgsConstructor
public class AuthenticationService {
  private final UserRepository userRepository;
  private final TokenRepository tokenRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;
  private final AuthenticationManager authenticationManager;

  /**
   * 注册
   * @param request RegisterRequest
   * @return AuthenticationResponse
   */
  public AuthenticationResponse register(RegisterRequest request) {
    var user = User.builder()
        .firstname(request.getFirstname())
        .lastname(request.getLastname())
        .email(request.getEmail())
        .password(passwordEncoder.encode(request.getPassword()))
        .role(request.getRole())
        .build();
    var savedUser = userRepository.save(user);
    var jwtToken = jwtService.generateToken(user);
    var refreshToken = jwtService.generateRefreshToken(user);
    saveUserToken(savedUser, jwtToken);
    return AuthenticationResponse.builder()
        .accessToken(jwtToken)
        .refreshToken(refreshToken)
        .build();
  }

  /**
   * 认证
   * @param request AuthenticationRequest
   * @return AuthenticationResponse
   */
  public AuthenticationResponse authenticate(AuthenticationRequest request) {
    authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(
            request.getEmail(),
            request.getPassword()
        )
    );
    var user = userRepository.findByEmail(request.getEmail()).orElseThrow();
    var jwtToken = jwtService.generateToken(user);
    var refreshToken = jwtService.generateRefreshToken(user);
    revokeAllUserTokens(user);
    saveUserToken(user, jwtToken);
    return AuthenticationResponse.builder()
        .accessToken(jwtToken)
        .refreshToken(refreshToken)
        .build();
  }

  /**
   * 保存用户 Token
   * @param user User
   * @param jwtToken String
   */
  private void saveUserToken(User user, String jwtToken) {
    var token = Token.builder()
        .user(user)
        .token(jwtToken)
        .tokenType(TokenType.BEARER)
        .expired(false)
        .revoked(false)
        .build();
    tokenRepository.save(token);
  }

  /**
   * 撤销指定用户所有的 Token
   * @param user User
   */
  private void revokeAllUserTokens(User user) {
    var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
    if (validUserTokens.isEmpty()) {
      return;
    }
    validUserTokens.forEach(token -> {
      token.setExpired(true);
      token.setRevoked(true);
    });
    tokenRepository.saveAll(validUserTokens);
  }

  /**
   * 刷新 Token。提交一个有效的 Refresh Token，返回一个新的 Access Token
   * @param request HttpServletRequest
   * @param response HttpServletResponse
   * @throws IOException IOException
   */
  public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // 从请求中提取有关认证的Header信息
    final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

    final String refreshToken;
    final String userEmail;
    if (authHeader == null ||!authHeader.startsWith("Bearer ")) {
      return;
    }
    // 从请求头中提取Jwt（即'Bearer '后的部分）
    refreshToken = authHeader.substring(7);
    // 从刷新Token中提取用户邮箱
    userEmail = jwtService.extractUsername(refreshToken);

    if (userEmail != null) {
      // 提取用户对象
      var user = this.userRepository.findByEmail(userEmail).orElseThrow();
      // 如果 Refresh Token有效，则生成新的 Access Token
      if (jwtService.isTokenValid(refreshToken, user)) {
        var accessToken = jwtService.generateToken(user);
        //撤销用户当前的所有 Token
        revokeAllUserTokens(user);
        // 保存新的 Access Token
        saveUserToken(user, accessToken);

        // 生成响应对象
        var authResponse = AuthenticationResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();

        // 将响应对象写入到响应流中
        new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
      }
    }
  }
}
