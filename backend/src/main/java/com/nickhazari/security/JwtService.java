package com.nickhazari.security;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.time.Duration;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.nickhazari.portfolio.dtos.UserDto;
import jakarta.annotation.PostConstruct;
import org.springframework.security.core.userdetails.UserDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {
  private static final Logger log = LoggerFactory.getLogger(JwtService.class);
  private static final long DEFAULT_ACCESS_EXPIRATION_MS = 900_000L;
  private static final long DEFAULT_REFRESH_EXPIRATION_MS = 1_209_600_000L;
  private static final Pattern SIMPLE_DURATION = Pattern.compile("^(\\d+)(ms|s|m|h|d)$");

  @Value("${spring.jwt.secret}")
  private String SECRET;

  @Value("${spring.jwt.access.expiration}")
  private String accessExpirationRaw;

  @Value("${spring.jwt.refresh.expiration}")
  private String refreshExpirationRaw;

  private long accessExpiration;
  private long refreshExpiration;

  @PostConstruct
  private void initExpirations() {
    accessExpiration = parseDurationMillis(
        accessExpirationRaw,
        DEFAULT_ACCESS_EXPIRATION_MS,
        "spring.jwt.access.expiration");
    refreshExpiration = parseDurationMillis(
        refreshExpirationRaw,
        DEFAULT_REFRESH_EXPIRATION_MS,
        "spring.jwt.refresh.expiration");
  }

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

  private long parseDurationMillis(String raw, long fallback, String propertyName) {
    if (raw == null) {
      log.warn("{} is not set; using fallback {} ms", propertyName, fallback);
      return fallback;
    }

    String cleaned = raw.split("#", 2)[0].trim();
    if (cleaned.isEmpty()) {
      log.warn("{} is empty; using fallback {} ms", propertyName, fallback);
      return fallback;
    }

    try {
      if (cleaned.startsWith("P") || cleaned.startsWith("p")) {
        return Duration.parse(cleaned).toMillis();
      }

      Matcher matcher = SIMPLE_DURATION.matcher(cleaned.toLowerCase(Locale.ROOT));
      if (matcher.matches()) {
        long value = Long.parseLong(matcher.group(1));
        String unit = matcher.group(2);
        return switch (unit) {
          case "ms" -> value;
          case "s" -> Math.multiplyExact(value, 1000L);
          case "m" -> Math.multiplyExact(value, 60_000L);
          case "h" -> Math.multiplyExact(value, 3_600_000L);
          case "d" -> Math.multiplyExact(value, 86_400_000L);
          default -> fallback;
        };
      }

      if (cleaned.contains("*")) {
        long total = 1L;
        for (String part : cleaned.split("\\*")) {
          String normalized = normalizeLongToken(part);
          long factor = Long.parseLong(normalized);
          total = Math.multiplyExact(total, factor);
        }
        return total;
      }

      return Long.parseLong(normalizeLongToken(cleaned));
    } catch (Exception ex) {
      log.warn("{} has invalid value '{}'; using fallback {} ms", propertyName, raw, fallback);
      return fallback;
    }
  }

  private String normalizeLongToken(String token) {
    return token.trim().replaceAll("(?i)L$", "");
  }

}
