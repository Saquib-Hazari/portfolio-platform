package com.nickhazari.portfolio.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.nickhazari.portfolio.dtos.AuthUserDto;
import com.nickhazari.portfolio.dtos.LoginRequest;
import com.nickhazari.portfolio.dtos.SignupRequest;
import com.nickhazari.services.AuthService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
@Validated
public class AuthController {
  private final AuthService authService;

  public AuthController(AuthService authService) {
    this.authService = authService;
  }

  @PostMapping("/signup")
  public ResponseEntity<AuthUserDto> signup(@Valid @RequestBody SignupRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request));
  }

  @PostMapping("/login")
  public ResponseEntity<AuthUserDto> login(@Valid @RequestBody LoginRequest request) {
    return ResponseEntity.ok(authService.authenticate(request));
  }

  @RequestMapping(value = "/logout", method = { RequestMethod.POST, RequestMethod.GET })
  public ResponseEntity<?> logout() {
    return ResponseEntity.ok().body("Logged Out!");
  }
}
