package com.nickhazari.portfolio.dtos;

import java.util.UUID;

import lombok.Data;

@Data
public class AuthUserDto {
  private UUID id;
  private String email;
  private String role;
  private String accessToken;
}
