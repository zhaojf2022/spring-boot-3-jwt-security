package com.alibou.security.controller;

import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 示例控制器
 */
@RestController
@RequestMapping("/api/v1/demo-controller")
@Hidden
public class DemoController {

  /**
   * sayHello 接口
   * @return ResponseEntity<String>
   */
  @GetMapping
  public ResponseEntity<String> sayHello() {

    return ResponseEntity.ok("Hello from secured endpoint");

  }

}
