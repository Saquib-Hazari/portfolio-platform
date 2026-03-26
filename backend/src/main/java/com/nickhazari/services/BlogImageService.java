package com.nickhazari.services;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.nickhazari.portfolio.entities.BlogImage;
import com.nickhazari.portfolio.exception.ConflictException;
import com.nickhazari.portfolio.exception.NotFoundException;
import com.nickhazari.portfolio.repositories.BlogImageRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class BlogImageService {
  private final BlogImageRepository blogImageRepository;

  public BlogImage storeImage(MultipartFile file) {
    BlogImage image = new BlogImage();
    image.setFileName(file.getOriginalFilename());
    image.setContentType(file.getContentType());
    try {
      image.setImageData(file.getBytes());
    } catch (IOException ex) {
      throw new RuntimeException("Failed to read uploaded image.", ex);
    }
    BlogImage saved = blogImageRepository.save(image);
    saved.setImageUrl("/api/uploads/images/" + saved.getId());
    return blogImageRepository.save(saved);
  }

  public Optional<BlogImage> findImage(UUID id) {
    return blogImageRepository.findById(id);
  }

  public void deleteImage(UUID id) {
    BlogImage image = blogImageRepository.findById(id)
      .orElseThrow(() -> new NotFoundException("Image not found."));
    if (image.getBlog() != null) {
      throw new ConflictException("Image is attached to a blog.");
    }
    blogImageRepository.delete(image);
  }
}
