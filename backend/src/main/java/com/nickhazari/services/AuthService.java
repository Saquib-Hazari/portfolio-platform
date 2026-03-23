package com.nickhazari.services;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.nickhazari.portfolio.dtos.AuthUserDto;
import com.nickhazari.portfolio.dtos.LoginRequest;
import com.nickhazari.portfolio.dtos.SignupRequest;
import com.nickhazari.portfolio.dtos.UserDto;
import com.nickhazari.portfolio.entities.User;
import com.nickhazari.portfolio.repositories.UserRepository;
import com.nickhazari.security.JwtService;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class AuthService {

  private final UserRepository userRepository;
  private final AuthenticationManager authenticationManager;
  private final JwtService jwtService;
  private final PasswordEncoder passwordEncoder;

  // =================
  // 🔐 LOGIN
  // =================
  public AuthUserDto authenticate(LoginRequest request) {

    // 1. Authenticate credentials
    authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(
            request.getEmail(),
            request.getPassword()));

    // 2. Fetch user
    User user = userRepository.findByEmail(request.getEmail())
        .orElseThrow(() -> new RuntimeException("User not found"));

    // 3. Generate token
    String token = jwtService.generateAccessToken(mapToUserDto(user));

    // 4. Return DTO
    return mapToDto(user, token);
  }

  // =================
  // 🆕 REGISTER
  // =================
  public AuthUserDto register(SignupRequest request) {

    // Check if user exists
    if (userRepository.findByEmail(request.getEmail()).isPresent()) {
      throw new RuntimeException("User already exists");
    }

    User user = new User();
    user.setUserName(request.getUsername());
    user.setEmail(request.getEmail());
    user.setPassword(passwordEncoder.encode(request.getPassword()));
    user.setRole("ROLE_USER");

    userRepository.saveAndFlush(user);

    String token = jwtService.generateAccessToken(mapToUserDto(user));

    return mapToDto(user, token);
  }

  // =================
  // 🔄 MAPPERS
  // =================

  private AuthUserDto mapToDto(User user, String token) {
    AuthUserDto dto = new AuthUserDto();
    dto.setId(user.getId());
    dto.setEmail(user.getEmail());
    dto.setRole(user.getRole());
    dto.setAccessToken(token);
    return dto;
  }

  private AuthUserDto mapToDto(User user) {
    AuthUserDto dto = new AuthUserDto();
    dto.setId(user.getId());
    dto.setEmail(user.getEmail());
    dto.setRole(user.getRole());
    return dto;
  }

  private UserDto mapToUserDto(User user) {
    UserDto dto = new UserDto();
    dto.setId(user.getId());
    dto.setUserName(user.getUserName());
    dto.setEmail(user.getEmail());
    dto.setRole(user.getRole());
    return dto;
  }
}
