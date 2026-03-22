package com.nickhazari.portfolio.dtos;

import java.util.UUID;

import lombok.Data;

@Data
public class UserDto {
  private UUID id;
  private String userName;
  private String email;
  private String role; // USER, ADMIN
}
