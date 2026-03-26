package com.nickhazari.services;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.nickhazari.portfolio.entities.Comment;
import com.nickhazari.portfolio.entities.CommentReaction;
import com.nickhazari.portfolio.entities.ReactionType;
import com.nickhazari.portfolio.entities.User;
import com.nickhazari.portfolio.exception.ConflictException;
import com.nickhazari.portfolio.exception.NotFoundException;
import com.nickhazari.portfolio.repositories.CommentReactionRepository;
import com.nickhazari.portfolio.repositories.CommentRepository;
import com.nickhazari.portfolio.repositories.UserRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class CommentReactionService {
  private final CommentReactionRepository commentReactionRepository;
  private final CommentRepository commentRepository;
  private final UserRepository userRepository;

  public void addReaction(UUID commentId, ReactionType type) {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth == null || auth.getName() == null) {
      throw new NotFoundException("User not found");
    }

    User user = userRepository.findByEmail(auth.getName())
      .orElseThrow(() -> new NotFoundException("User not found"));

    Comment comment = commentRepository.findById(commentId)
      .orElseThrow(() -> new NotFoundException("Comment not found"));

    if (commentReactionRepository.existsByCommentIdAndUserIdAndReactionType(
        commentId, user.getId(), type)) {
      throw new ConflictException("You already reacted.");
    }

    CommentReaction reaction = new CommentReaction();
    reaction.setComment(comment);
    reaction.setUser(user);
    reaction.setReactionType(type);
    reaction.setCreatedAt(LocalDateTime.now());
    commentReactionRepository.save(reaction);
  }
}
