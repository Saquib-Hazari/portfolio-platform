package com.nickhazari.portfolio.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nickhazari.portfolio.dtos.CommentDto;
import com.nickhazari.portfolio.dtos.CommentRequest;
import com.nickhazari.services.CommentService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/blogs/{blogId}/comments")
@Validated
@AllArgsConstructor
public class CommentController {
  private final CommentService commentService;

  @GetMapping
  public ResponseEntity<List<CommentDto>> getComments(@PathVariable UUID blogId) {
    return ResponseEntity.ok(commentService.getComments(blogId));
  }

  @PostMapping
  public ResponseEntity<CommentDto> addComment(
      @PathVariable UUID blogId,
      @Valid @RequestBody CommentRequest request) {
    CommentDto created = commentService.addComment(blogId, request.getContent());
    return ResponseEntity.status(HttpStatus.CREATED).body(created);
  }

  @DeleteMapping("/{commentId}")
  public ResponseEntity<Void> deleteComment(@PathVariable UUID commentId) {
    commentService.deleteComment(commentId);
    return ResponseEntity.noContent().build();
  }
}
