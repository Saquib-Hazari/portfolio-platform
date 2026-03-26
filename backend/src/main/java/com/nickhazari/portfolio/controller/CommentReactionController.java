package com.nickhazari.portfolio.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nickhazari.portfolio.dtos.CommentReactionRequest;
import com.nickhazari.portfolio.entities.ReactionType;
import com.nickhazari.portfolio.exception.BadRequestException;
import com.nickhazari.services.CommentReactionService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/comments/{commentId}/reactions")
@Validated
@AllArgsConstructor
public class CommentReactionController {
  private final CommentReactionService commentReactionService;

  @PostMapping
  public ResponseEntity<Void> addReaction(
      @PathVariable UUID commentId,
      @Valid @RequestBody CommentReactionRequest request) {
    ReactionType type;
    try {
      type = ReactionType.valueOf(request.getReaction().toUpperCase());
    } catch (IllegalArgumentException ex) {
      throw new BadRequestException("Invalid reaction type");
    }
    commentReactionService.addReaction(commentId, type);
    return ResponseEntity.ok().build();
  }
}
