package com.nickhazari.portfolio.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nickhazari.portfolio.dtos.BlogDto;
import com.nickhazari.services.BlogService;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/blogs")
@CrossOrigin(origins = "*")
@AllArgsConstructor
public class BlogController {
  private final BlogService blogService;

  // CREATE
  @PostMapping
  public ResponseEntity<BlogDto> createBlog(@RequestBody BlogDto blog) {
    return ResponseEntity.ok(blogService.createBlog(blog));
  }

  // READ
  @GetMapping
  public List<BlogDto> getAllBlogs() {
    return blogService.getAllBlogs();
  }

  // UPDATE
  @PutMapping("/{id}")
  public BlogDto updateBlog(@PathVariable UUID id, @RequestBody BlogDto blog) {
    return blogService.updateBlog(id, blog);
  }

  // DELETE
  @DeleteMapping("/{id}")
  public void deleteBlog(@PathVariable UUID id) {
    blogService.deleteBlog(id);
  }
}
