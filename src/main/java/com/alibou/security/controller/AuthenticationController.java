package com.alibou.security.controller;

import com.alibou.security.auth.AuthenticationRequest;
import com.alibou.security.auth.AuthenticationResponse;
import com.alibou.security.auth.AuthenticationService;
import com.alibou.security.auth.RegisterRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * 认证控制器
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

  private final AuthenticationService service;

  /**
   * 注册
   * @param request RegisterRequest
   * @return ResponseEntity<AuthenticationResponse>
   */
  @PostMapping("/register")
  public ResponseEntity<AuthenticationResponse> register(
      @RequestBody RegisterRequest request) {

    return ResponseEntity.ok(service.register(request));
  }

  /**
   * 认证
   * @param request AuthenticationRequest
   * @return ResponseEntity<AuthenticationResponse>
   */
  @PostMapping("/authenticate")
  public ResponseEntity<AuthenticationResponse> authenticate(
      @RequestBody AuthenticationRequest request) {

    return ResponseEntity.ok(service.authenticate(request));
  }

  /**
   * 刷新 token
   * @param request HttpServletRequest
   * @param response HttpServletResponse
   * @throws IOException IO异常
   */
  @PostMapping("/refresh-token")
  public void refreshToken(
      HttpServletRequest request,
      HttpServletResponse response) throws IOException {

    service.refreshToken(request, response);
  }


}
