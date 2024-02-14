# 使用JWT实现Spring Boot 3.0的安全性
这个项目演示了如何使用Spring Boot 3.0和JSON Web Tokens (JWT)实现安全性。它包括以下特性:
forked from [ali-bouali/spring-boot-3-jwt-security](https://github.com/ali-bouali/spring-boot-3-jwt-security)

## 特性
*使用JWT认证进行用户注册和登录
*使用BCrypt进行密码加密
*使用Spring Security实现基于角色的授权
*自定义的拒绝访问处理
*注销机制
*刷新令牌

## 技术
* Spring Boot 3.2.2
* Spring Security
* JSON Web Tokens (JWT) 0.12.5
* BCrypt
* Maven
* OpenAPI 3.0 (Swagger)

## 入门
要开始这个项目，你需要在本地机器上安装以下软件:
* JDK 17+
* Maven 3+

要构建并运行项目，请遵循以下步骤。

* 克隆仓库:`git Clone https://github.com/zhaojf2022/spring-boot-3-jwt-security.git`
* 进入项目目录:cd spring-boot-3-jwt-security
* 在本机的 mysql中，创建 “jwt_security” 数据库
* 构建项目:mvn clean install
* 本机运行项目:mvn spring-boot: Run
* 浏览器中查看项目接口: http://localhost:8080/swagger-ui.html
