package com.nickhazari.portfolio.repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nickhazari.portfolio.entities.Comment;

public interface CommentRepository extends JpaRepository<Comment, UUID> {
  List<Comment> findByBlogIdOrderByCreatedAtDesc(UUID blogId);
  boolean existsByBlogIdAndUserId(UUID blogId, UUID userId);
  List<Comment> findTop50ByOrderByCreatedAtDesc();
}
