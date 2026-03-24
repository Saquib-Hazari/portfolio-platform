package com.nickhazari.portfolio.dtos;

import java.util.List;
import java.util.UUID;

import lombok.Data;

@Data
public class BlogDto {
  private UUID id;
  private UserDto author;
  private String title;
  private String subtitle;
  private String description;
  private String code;
  private String image;
  private List<String> tags;
}
