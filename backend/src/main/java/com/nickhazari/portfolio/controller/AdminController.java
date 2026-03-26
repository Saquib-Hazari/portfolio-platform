package com.nickhazari.portfolio.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nickhazari.portfolio.dtos.AdminStatsDto;
import com.nickhazari.portfolio.dtos.AdminCommentDto;
import com.nickhazari.portfolio.repositories.BlogRepository;
import com.nickhazari.portfolio.repositories.CommentRepository;
import com.nickhazari.portfolio.repositories.UserRepository;
import com.nickhazari.services.CommentService;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/admin")
@AllArgsConstructor
public class AdminController {
  private final UserRepository userRepository;
  private final BlogRepository blogRepository;
  private final CommentRepository commentRepository;
  private final CommentService commentService;

  @GetMapping("/stats")
  public ResponseEntity<AdminStatsDto> getStats() {
    AdminStatsDto stats = new AdminStatsDto(
        userRepository.count(),
        blogRepository.count(),
        commentRepository.count());
    return ResponseEntity.ok(stats);
  }

  @GetMapping("/comments")
  public ResponseEntity<java.util.List<AdminCommentDto>> getRecentComments() {
    var comments = commentRepository.findTop50ByOrderByCreatedAtDesc()
      .stream()
      .map(comment -> new AdminCommentDto(
        comment.getId(),
        comment.getBlog().getId(),
        comment.getBlog().getTitle(),
        comment.getUser().getId(),
        comment.getUser().getUserName(),
        comment.getUser().getEmail(),
        comment.getContent(),
        comment.getCreatedAt()))
      .toList();
    return ResponseEntity.ok(comments);
  }

  @DeleteMapping("/comments/{commentId}")
  public ResponseEntity<Void> deleteComment(@PathVariable java.util.UUID commentId) {
    commentService.deleteComment(commentId);
    return ResponseEntity.noContent().build();
  }
}
