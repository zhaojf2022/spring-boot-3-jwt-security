package com.alibou.security.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 管理接口
 */
@RestController
@RequestMapping("/api/v1/management")
@Tag(name = "Management")
public class ManagementController {

    /**
     * GET 接口
     * '@Operation'注解用于描述一个操作，通常是针对特定路径的HTTP方法。
     * @return String
     */
    @Operation(
        description = "Get endpoint for manager",
        summary = "This is a summary for management get endpoint",
        responses = {
            @ApiResponse(
                description = "Success",
                responseCode = "200"
            ),
            @ApiResponse(
                description = "Unauthorized / Invalid Token",
                responseCode = "403"
            )
        }
    )
    @GetMapping
    public String get() {
        return "GET:: management controller";
    }

    /**
     * POST 接口
     * @return String
     */
    @PostMapping
    public String post() {
        return "POST:: management controller";
    }

    /**
     * PUT 接口
     * @return String
     */
    @PutMapping
    public String put() {
        return "PUT:: management controller";
    }

    /**
     * DELETE 接口
     * @return String
     */
    @DeleteMapping
    public String delete() {
        return "DELETE:: management controller";
    }
}
