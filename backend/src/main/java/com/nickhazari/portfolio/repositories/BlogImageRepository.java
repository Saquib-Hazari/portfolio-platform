package com.nickhazari.portfolio.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nickhazari.portfolio.entities.BlogImage;

public interface BlogImageRepository extends JpaRepository<BlogImage, UUID> {
  BlogImage findFirstByFileName(String fileName);

  interface OrphanedImageView {
    UUID getId();
    String getFileName();
    String getContentType();
    String getImageUrl();
  }

  java.util.List<OrphanedImageView> findByBlogIsNull();
}
