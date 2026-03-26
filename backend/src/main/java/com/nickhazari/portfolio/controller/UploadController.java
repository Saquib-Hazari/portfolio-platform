package com.nickhazari.portfolio.controller;

import java.util.UUID;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.nickhazari.portfolio.dtos.UploadResponse;
import com.nickhazari.portfolio.dtos.OrphanedImageDto;
import com.nickhazari.portfolio.entities.BlogImage;
import com.nickhazari.portfolio.exception.NotFoundException;
import com.nickhazari.portfolio.repositories.BlogImageRepository;
import com.nickhazari.services.BlogImageService;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/uploads")
@CrossOrigin(origins = "*")
@AllArgsConstructor
public class UploadController {
  private final BlogImageService blogImageService;
  private final BlogImageRepository blogImageRepository;

  @PostMapping("/images")
  public ResponseEntity<UploadResponse> uploadImage(@RequestParam("file") MultipartFile file) {
    BlogImage image = blogImageService.storeImage(file);
    String url = ServletUriComponentsBuilder.fromCurrentContextPath()
      .path("/api/uploads/images/")
      .path(image.getId().toString())
      .toUriString();
    return ResponseEntity.status(HttpStatus.CREATED).body(new UploadResponse(url, image.getId().toString()));
  }

  @GetMapping("/images/{id}")
  public ResponseEntity<byte[]> getImage(@PathVariable UUID id) {
    BlogImage image = blogImageService.findImage(id)
      .orElseThrow(() -> new NotFoundException("Image not found."));
    MediaType contentType = MediaType.APPLICATION_OCTET_STREAM;
    if (image.getContentType() != null && !image.getContentType().isBlank()) {
      contentType = MediaType.parseMediaType(image.getContentType());
    }
    return ResponseEntity.ok()
      .contentType(contentType)
      .body(image.getImageData());
  }

  @DeleteMapping("/images/{id}")
  public ResponseEntity<Void> deleteImage(@PathVariable UUID id) {
    blogImageService.deleteImage(id);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/images/orphaned")
  public ResponseEntity<List<OrphanedImageDto>> getOrphanedImages() {
    List<OrphanedImageDto> images = blogImageRepository.findByBlogIsNull().stream()
      .map(view -> new OrphanedImageDto(
        view.getId(),
        view.getFileName(),
        view.getContentType(),
        view.getImageUrl()))
      .toList();
    return ResponseEntity.ok(images);
  }

}
