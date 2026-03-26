package com.nickhazari.portfolio.dtos;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BlogCreateRequest {
  @NotNull(message = "author is required")
  @Valid
  private UserDto author;

  @NotBlank(message = "title is required")
  private String title;

  private String subtitle;

  @NotBlank(message = "description is required")
  private String description;

  private String code;
  private String image;
  private List<String> tags;
}
