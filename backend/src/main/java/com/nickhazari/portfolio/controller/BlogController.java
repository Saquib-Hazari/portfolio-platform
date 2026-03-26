package com.nickhazari.portfolio.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nickhazari.portfolio.dtos.BlogCreateRequest;
import com.nickhazari.portfolio.dtos.BlogDto;
import com.nickhazari.portfolio.dtos.BlogUpdateRequest;
import com.nickhazari.services.BlogService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/blogs")
@CrossOrigin(origins = "*")
@Validated
@AllArgsConstructor
public class BlogController {
  private final BlogService blogService;

  // CREATE
  @PostMapping
  public ResponseEntity<BlogDto> createBlog(@Valid @RequestBody BlogCreateRequest blog) {
    return ResponseEntity.status(HttpStatus.CREATED).body(blogService.createBlog(blog));
  }

  // READ
  @GetMapping
  public List<BlogDto> getAllBlogs() {
    return blogService.getAllBlogs();
  }

  @GetMapping("/{id}")
  public ResponseEntity<BlogDto> getBlog(@PathVariable UUID id) {
    return ResponseEntity.ok(blogService.getBlog(id));
  }

  // UPDATE
  @PutMapping("/{id}")
  public ResponseEntity<BlogDto> updateBlog(@PathVariable UUID id, @Valid @RequestBody BlogUpdateRequest blog) {
    return ResponseEntity.ok(blogService.updateBlog(id, blog));
  }

  // DELETE
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteBlog(@PathVariable UUID id) {
    blogService.deleteBlog(id);
    return ResponseEntity.noContent().build();
  }
}
