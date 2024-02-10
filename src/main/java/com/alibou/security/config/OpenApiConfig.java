package com.alibou.security.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;

/**
 * OpenApi配置类。使用注解提供API的元数据和安全方案。
 */

// @OpenAPIDefinition注解它是一个组合注解，包含@Info和@Server注解。
@OpenAPIDefinition(
    // @info注解用于提供API的一般信息，如API的标题、版本和API提供者的联系方式。
    info = @Info(
        contact = @Contact(
                name = "Alibou",
                email = "contact@aliboucoding.com",
                url = "https://aliboucoding.com/course"
        ),
        description = "OpenApi documentation for Spring Security",
        title = "OpenApi specification - Alibou",
        version = "1.0",
        license = @License(
                name = "Licence name",
                url = "https://some-url.com"
        ),
        termsOfService = "Terms of service"
    ),
    // @servers注解用于提供API的服务器信息。
    servers = {
        // 本地环境（测试）
        @Server(
            description = "Local ENV",
            url = "http://localhost:8080"
        ),
        // 生产环境
        @Server(
            description = "PROD ENV",
            url = "https://aliboucoding.com/course"
        )
    },
    // @security注解用于提供API的安全要求。
    security = {
        // 指定了一个名为bearerAuth的安全要求，它是一个HTTP类型的安全方案，使用JWT作为认证方案。
        @SecurityRequirement(
            name = "bearerAuth"
        )
    }
)
// @SecurityScheme注解用于定义一个名为bearerAuth的安全方案
@SecurityScheme(
    name = "bearerAuth",
    description = "JWT auth description",
    scheme = "bearer",
    type = SecuritySchemeType.HTTP,
    bearerFormat = "JWT",
    // in属性指定了安全方案位于HEADER中
    in = SecuritySchemeIn.HEADER
)
public class OpenApiConfig {
}
