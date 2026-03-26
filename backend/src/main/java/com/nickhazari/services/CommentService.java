package com.nickhazari.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.nickhazari.portfolio.dtos.CommentDto;
import com.nickhazari.portfolio.entities.Blog;
import com.nickhazari.portfolio.entities.Comment;
import com.nickhazari.portfolio.entities.User;
import com.nickhazari.portfolio.entities.ReactionType;
import com.nickhazari.portfolio.exception.BadRequestException;
import com.nickhazari.portfolio.exception.ConflictException;
import com.nickhazari.portfolio.exception.NotFoundException;
import com.nickhazari.portfolio.repositories.BlogRepository;
import com.nickhazari.portfolio.repositories.CommentRepository;
import com.nickhazari.portfolio.repositories.CommentReactionRepository;
import com.nickhazari.portfolio.repositories.UserRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class CommentService {
  private final CommentRepository commentRepository;
  private final CommentReactionRepository commentReactionRepository;
  private final BlogRepository blogRepository;
  private final UserRepository userRepository;

  public List<CommentDto> getComments(UUID blogId) {
    User currentUser = getCurrentUserOrNull();
    return commentRepository.findByBlogIdOrderByCreatedAtDesc(blogId)
      .stream()
      .map(comment -> mapToDto(comment, currentUser))
      .toList();
  }

  public CommentDto addComment(UUID blogId, String content) {
    if (content == null || content.isBlank()) {
      throw new BadRequestException("comment is required");
    }

    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth == null || auth.getName() == null) {
      throw new BadRequestException("Authentication required");
    }

    User user = userRepository.findByEmail(auth.getName())
      .orElseThrow(() -> new NotFoundException("User not found"));

    Blog blog = blogRepository.findById(blogId)
      .orElseThrow(() -> new NotFoundException("Blog not found"));

    if (isRoleUser(user) && commentRepository.existsByBlogIdAndUserId(blogId, user.getId())) {
      throw new ConflictException("You can only comment once on this blog.");
    }

    Comment comment = new Comment();
    comment.setBlog(blog);
    comment.setUser(user);
    comment.setContent(content.trim());
    comment.setCreatedAt(LocalDateTime.now());

    Comment saved = commentRepository.save(comment);
    return mapToDto(saved, user);
  }

  public void deleteComment(UUID id) {
    if (!commentRepository.existsById(id)) {
      throw new NotFoundException("Comment not found");
    }
    commentRepository.deleteById(id);
  }

  private boolean isRoleUser(User user) {
    String role = user.getRole();
    if (role == null) {
      return false;
    }
    return "ROLE_USER".equals(role) || "USER".equals(role);
  }

  private CommentDto mapToDto(Comment comment, User currentUser) {
    CommentDto dto = new CommentDto();
    dto.setId(comment.getId());
    dto.setUserId(comment.getUser().getId());
    dto.setUserName(comment.getUser().getUserName());
    dto.setUserEmail(comment.getUser().getEmail());
    dto.setContent(comment.getContent());
    dto.setCreatedAt(comment.getCreatedAt());
    dto.setLikes(commentReactionRepository.countByCommentIdAndReactionType(comment.getId(), ReactionType.LIKE));
    dto.setDislikes(commentReactionRepository.countByCommentIdAndReactionType(comment.getId(), ReactionType.DISLIKE));
    dto.setHearts(commentReactionRepository.countByCommentIdAndReactionType(comment.getId(), ReactionType.HEART));
    if (currentUser != null) {
      dto.setLikedByMe(commentReactionRepository.existsByCommentIdAndUserIdAndReactionType(
        comment.getId(), currentUser.getId(), ReactionType.LIKE));
      dto.setDislikedByMe(commentReactionRepository.existsByCommentIdAndUserIdAndReactionType(
        comment.getId(), currentUser.getId(), ReactionType.DISLIKE));
      dto.setHeartedByMe(commentReactionRepository.existsByCommentIdAndUserIdAndReactionType(
        comment.getId(), currentUser.getId(), ReactionType.HEART));
    }
    return dto;
  }

  private User getCurrentUserOrNull() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth == null || auth.getName() == null || "anonymousUser".equals(auth.getName())) {
      return null;
    }
    return userRepository.findByEmail(auth.getName()).orElse(null);
  }
}
