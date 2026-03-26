package com.nickhazari.security;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

class JwtServiceTest {

  @Test
  void parsesDurationExpressionsAndComments() {
    JwtService jwtService = new JwtService();

    long fallback = 123L;

    assertEquals(900_000L, parse(jwtService, "1000*60*15#15mins", fallback));
    assertEquals(86_400_000L, parse(jwtService, "1000L * 60 * 60 * 24 # 1 day", fallback));
    assertEquals(900_000L, parse(jwtService, "15m", fallback));
    assertEquals(1_800_000L, parse(jwtService, "PT30M", fallback));
    assertEquals(fallback, parse(jwtService, "not-a-number", fallback));
  }

  private long parse(JwtService jwtService, String raw, long fallback) {
    return (long) ReflectionTestUtils.invokeMethod(
        jwtService,
        "parseDurationMillis",
        raw,
        fallback,
        "test.prop");
  }
}
