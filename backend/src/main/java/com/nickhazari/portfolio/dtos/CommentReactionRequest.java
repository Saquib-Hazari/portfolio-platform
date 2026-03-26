package com.nickhazari.portfolio.dtos;

import jakarta.validation.constraints.NotBlank;

public class CommentReactionRequest {
  @NotBlank(message = "reaction is required")
  private String reaction;

  public String getReaction() {
    return reaction;
  }

  public void setReaction(String reaction) {
    this.reaction = reaction;
  }
}
