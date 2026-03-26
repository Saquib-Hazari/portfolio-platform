package com.nickhazari.portfolio.validation;

import com.nickhazari.portfolio.dtos.BlogUpdateRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class AtLeastOneFieldValidator implements ConstraintValidator<AtLeastOneField, BlogUpdateRequest> {
  @Override
  public boolean isValid(BlogUpdateRequest value, ConstraintValidatorContext context) {
    if (value == null) {
      return true;
    }
    return value.getAuthor() != null
        || value.getTitle() != null
        || value.getSubtitle() != null
        || value.getDescription() != null
        || value.getCode() != null
        || value.getImage() != null
        || value.getTags() != null;
  }
}
