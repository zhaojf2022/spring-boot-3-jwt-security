package com.alibou.security.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;

/**
 * Jwt 服务
 */
@Service
public class JwtService {

  @Value("${application.security.jwt.secret-key}")
  private String secretKey;
  @Value("${application.security.jwt.expiration}")
  private long jwtExpiration;
  @Value("${application.security.jwt.refresh-token.expiration}")
  private long refreshExpiration;

  /**
   * 从 token 中提取用户名
   * @param token String
   * @return String
   */
  public String extractUsername(String token) {

    // 从声明中提取用户名
    return extractClaim(token, Claims::getSubject);
  }

  /**
   * 从 token 中提取声明
   * @param token String
   * @param claimsResolver Function<Claims, T> 函数式接口，提取具体声明的函数
   * @return T
   */
  public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
    // 先提取所有的声明
    final Claims claims = extractAllClaims(token);
    // 使用传入的函数，从所有声明中提取特定的声明
    return claimsResolver.apply(claims);
  }

  /**
   * 生成默认的token（不指定附加的声明）
   * @param userDetails UserDetails
   * @return String
   */
  public String generateToken(UserDetails userDetails) {

    // 使用空的声明，生成 token
    return generateToken(new HashMap<>(), userDetails);
  }

  /**
   * 使用指定的声明（Map结构的数据）,生成 token
   * @param extraClaims Map<String, Object>
   * @param userDetails UserDetails
   * @return String
   */
  public String generateToken(
      Map<String, Object> extraClaims,
      UserDetails userDetails) {

    return buildToken(extraClaims, userDetails, jwtExpiration);
  }

  /**
   * 生成刷新 token
   * @param userDetails UserDetails
   * @return String
   */
  public String generateRefreshToken(UserDetails userDetails) {
    return buildToken(new HashMap<>(), userDetails, refreshExpiration);
  }

  /**
   * 创建 token
   * @param extraClaims Map<String, Object>
   * @param userDetails UserDetails
   * @return String
   */
  private String buildToken(
          Map<String, Object> extraClaims,
          UserDetails userDetails,
            long expiration) {

    return Jwts.builder()
        // 设置用户名
        .subject(userDetails.getUsername())
        // 添加额外声明
        .claims().add(extraClaims).and()
        // 设置签发时间
        .issuedAt(new Date(System.currentTimeMillis()))
        // 设置过期时间
        .expiration(Date.from(Instant.now().plus(expiration, ChronoUnit.MILLIS)))
        // 使用密钥签名
        .signWith(getSignInKey())
        // 压缩
        .compact();
  }

  /**
   * 根据密钥生成 SecretKey 对象，用于JWT签名和加密
   */
  private SecretKey getSecretKey(String secretKey) {

    //使用 Keys.hmacShaKeyFor()方法生密钥对象
    byte[] keyBytes = Base64.getDecoder().decode(secretKey);
    return Keys.hmacShaKeyFor(keyBytes);
  }


  /**
   * 验证 token
   * @param token String
   * @param userDetails UserDetails
   * @return boolean
   */
  public boolean isTokenValid(String token, UserDetails userDetails) {
    final String username = extractUsername(token);
    return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
  }

  /**
   * 判断 token 是否过期
   * @param token String
   * @return Date
   */
  private boolean isTokenExpired(String token) {
    return extractExpiration(token).before(new Date());
  }

  /**
   * 从 token 中提取过期时间
   * @param token String
   * @return Date
   */
  private Date extractExpiration(String token) {
    return extractClaim(token, Claims::getExpiration);
  }

  /**
   * 从 token 中提取所有声明
   * @param token String
   * @return Claims
   */
  private Claims extractAllClaims(String token) {

    // 获取密钥
    SecretKey key = getSecretKey(secretKey);

    return Jwts.parser()
        // 验证签名
        .verifyWith(key).build()
        // 解析签名
        .parseSignedClaims(token)
        // 获取声明
        .getPayload();

  }

  /**
   * 获取签名密钥
   * @return Key
   */
  private Key getSignInKey() {
    // 使用密钥生成器生成密钥
    byte[] keyBytes = Decoders.BASE64.decode(secretKey);
    // 使用密钥工厂生成密钥对象
    return Keys.hmacShaKeyFor(keyBytes);
  }
}
