package com.nickhazari.portfolio.dtos;

import java.util.List;

import com.nickhazari.portfolio.validation.AtLeastOneField;
import jakarta.validation.Valid;
import lombok.Data;

@Data
@AtLeastOneField
public class BlogUpdateRequest {
  @Valid
  private UserDto author;
  private String title;
  private String subtitle;
  private String description;
  private String code;
  private String image;
  private List<String> tags;
}
