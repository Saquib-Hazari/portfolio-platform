package com.nickhazari.portfolio.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nickhazari.portfolio.dtos.AuthUserDto;
import com.nickhazari.portfolio.dtos.LoginRequest;
import com.nickhazari.portfolio.dtos.SignupRequest;
import com.nickhazari.services.AuthService;

@RestController
@RequestMapping("/auth")
public class AuthController {
  private final AuthService authService;

  public AuthController(AuthService authService) {
    this.authService = authService;
  }

  @PostMapping("/signup")
  public AuthUserDto signup(@RequestBody SignupRequest request) {
    return authService.register(request);
  }

  @PostMapping("/login")
  public AuthUserDto login(@RequestBody LoginRequest request) {
    return authService.authenticate(request);
  }

  @RequestMapping(value = "/logout", method = { RequestMethod.POST, RequestMethod.GET })
  public ResponseEntity<?> logout() {
    return ResponseEntity.ok().body("Logged out");
  }
}
