package com.nickhazari.portfolio.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nickhazari.portfolio.entities.CommentReaction;
import com.nickhazari.portfolio.entities.ReactionType;

public interface CommentReactionRepository extends JpaRepository<CommentReaction, UUID> {
  long countByCommentIdAndReactionType(UUID commentId, ReactionType reactionType);
  boolean existsByCommentIdAndUserIdAndReactionType(UUID commentId, UUID userId, ReactionType reactionType);
}
