package com.nickhazari.security;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.nickhazari.portfolio.dtos.UserDto;
import org.springframework.security.core.userdetails.UserDetails;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {
  @Value("${spring.jwt.secret}")
  private String SECRET;

  @Value("${spring.jwt.access.expiration}")
  private long accessExpiration;

  @Value("${spring.jwt.refresh.expiration}")
  private long refreshExpiration;

  private SecretKey getSignKey() {
    byte[] keyBytes = Decoders.BASE64.decode(SECRET);
    return Keys.hmacShaKeyFor(keyBytes);
  }

  public String generateAccessToken(UserDto user) {

    Map<String, Object> claims = new HashMap<>();
    claims.put("email", user.getEmail());
    claims.put("userId", String.valueOf(user.getId()));

    return Jwts.builder()
        .subject(user.getEmail())
        .claims(claims)
        .issuedAt(new Date())
        .expiration(new Date(System.currentTimeMillis() + accessExpiration))
        .signWith(getSignKey())
        .compact();
  }

  public String generateRefreshToken(UserDto user) {
    return Jwts.builder()
        .subject(user.getEmail())
        .issuedAt(new Date())
        .expiration(new Date(System.currentTimeMillis() + refreshExpiration))
        .signWith(getSignKey())
        .compact();
  }

  // =====================
  // Claim Extraction
  // =====================

  public String extractUserId(String token) {
    return extractClaim(token, claims -> claims.get("userId", String.class));
  }

  public String extractEmail(String token) {
    return extractClaim(token, Claims::getSubject);
  }

  public Date extractExpiration(String token) {
    return extractClaim(token, Claims::getExpiration);
  }

  private <T> T extractClaim(String token, Function<Claims, T> resolver) {
    Claims claims = extractAllClaims(token);
    return resolver.apply(claims);
  }

  // ==================
  // Token Validation
  // ==================
  public boolean isTokenValid(String token, UserDetails userDetails) {
    String email = extractEmail(token);
    return email.equals(userDetails.getUsername()) && !isTokenExpired(token);
  }

  public boolean isTokenExpired(String token) {
    return extractExpiration(token).before(new Date());
  }

  // ===============
  // Token Parsing
  // ===============

  private Claims extractAllClaims(String token) {
    return Jwts.parser()
        .verifyWith(getSignKey())
        .build()
        .parseSignedClaims(token)
        .getPayload();
  }

}
