package edu.capstone4.userserver.jwt;

import java.util.Date;

import edu.capstone4.userserver.services.UserDetailsImpl;
import io.jsonwebtoken.security.SecureDigestAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;

@Component
public class JwtUtils {
  private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

  @Value("${meditracker.app.jwtSecret}")
  private String jwtSecret;

  @Value("${meditracker.app.jwtExpirationMs}")
  private int jwtExpirationMs;

  public String generateJwtToken(Authentication authentication) {

    UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();

    // 使用 Jwts.SIG.HS256 获取标准的 JWA 安全摘要算法
    SecureDigestAlgorithm<SecretKey, ?> algorithm = Jwts.SIG.HS256;

    return Jwts.builder()
            .subject((userPrincipal.getUsername()))
            .claim("userId", userPrincipal.getId())
            .issuedAt(new Date())
            .expiration(new Date((new Date()).getTime() + jwtExpirationMs))
            .signWith(key(), algorithm)
        .compact();
  }

  // 获取签名的密钥
  private SecretKey key() {
    // 使用 Keys.hmacShaKeyFor 来生成 SecretKey，适用于 HS256
    return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret)); // 使用 Decoders 来解码 Base64 编码的密钥
  }

  public String getUserNameFromJwtToken(String token) {
    // 使用 JwtParserBuilder 构建 JwtParser
    JwtParser jwtParser = Jwts.parser()  // 使用 parser() 返回 JwtParserBuilder
            .verifyWith(key()) // 设置签名密钥
            .build();
    Claims claims = jwtParser.parseSignedClaims(token).getPayload();
    return claims.getSubject();
  }

  public Long getUserIdFromJwtToken(String token) {
    JwtParser jwtParser = Jwts.parser()
            .verifyWith(key())
            .build();
    Claims claims = jwtParser.parseSignedClaims(token).getPayload();
    return claims.get("userId", Long.class); // 获取并返回 userId
  }

  public boolean validateJwtToken(String authToken) {
    try {
      JwtParser jwtParser = Jwts.parser()  // 使用 parser() 返回 JwtParserBuilder
              .verifyWith(key()) // 使用 verifyWith() 并传递 SecretKey
              .build();  // 使用 build() 创建 JwtParser 实例

      jwtParser.parseSignedClaims(authToken); // 使用 parseClaimsJws() 验证并解析 token
      return true;
    } catch (MalformedJwtException e) {
      logger.error("Invalid JWT token: {}", e.getMessage());
    } catch (ExpiredJwtException e) {
      logger.error("JWT token is expired: {}", e.getMessage());
    } catch (UnsupportedJwtException e) {
      logger.error("JWT token is unsupported: {}", e.getMessage());
    } catch (IllegalArgumentException e) {
      logger.error("JWT claims string is empty: {}", e.getMessage());
    }

    return false;
  }
}
