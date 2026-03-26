package com.nickhazari.portfolio.dtos;

import java.util.UUID;

import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class UserDto {
  private UUID id;
  private String userName;

  @Email(message = "email must be valid")
  private String email;
  private String role; // USER, ADMIN
}
