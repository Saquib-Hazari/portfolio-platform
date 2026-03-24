package com.nickhazari.services;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.nickhazari.portfolio.dtos.BlogDto;
import com.nickhazari.portfolio.dtos.UserDto;
import com.nickhazari.portfolio.entities.Blog;
import com.nickhazari.portfolio.entities.BlogImage;
import com.nickhazari.portfolio.entities.BlogTag;
import com.nickhazari.portfolio.entities.User;
import com.nickhazari.portfolio.repositories.BlogRepository;
import com.nickhazari.portfolio.repositories.UserRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class BlogService {
  private final BlogRepository blogRepository;
  private final UserRepository userRepository;

  public BlogDto createBlog(BlogDto blogDto) {
    Blog blog = mapToEntity(blogDto);
    blog.setCreatedAt(LocalDate.now());
    Blog saved = blogRepository.save(blog);
    return mapToDto(saved);
  }

  public List<BlogDto> getAllBlogs() {
    return blogRepository.findAll().stream().map(this::mapToDto).toList();
  }

  public BlogDto updateBlog(UUID id, BlogDto updated) {
    Blog blog = blogRepository.findById(id).orElseThrow(() -> new RuntimeException("Blog not found."));

    blog.setTitle(updated.getTitle());
    blog.setSubtitle(updated.getSubtitle());
    blog.setDescription(updated.getDescription());
    blog.setCode(updated.getCode());
    blog.setImage(updated.getImage());
    if (updated.getAuthor() != null) {
      blog.setAuthor(resolveAuthor(updated.getAuthor()));
    }
    blog.setTags(buildTags(updated.getTags(), blog));
    blog.setImages(buildImages(updated.getImage(), blog));

    Blog saved = blogRepository.save(blog);
    return mapToDto(saved);
  }

  public void deleteBlog(UUID id) {
    blogRepository.deleteById(id);
  }

  private Blog mapToEntity(BlogDto dto) {
    Blog blog = new Blog();
    blog.setTitle(dto.getTitle());
    blog.setSubtitle(dto.getSubtitle());
    blog.setDescription(dto.getDescription());
    blog.setCode(dto.getCode());
    blog.setImage(dto.getImage());
    blog.setAuthor(resolveAuthor(dto.getAuthor()));
    blog.setTags(buildTags(dto.getTags(), blog));
    blog.setImages(buildImages(dto.getImage(), blog));
    return blog;
  }

  private BlogDto mapToDto(Blog blog) {
    BlogDto dto = new BlogDto();
    dto.setId(blog.getId());
    dto.setAuthor(mapToDto(blog.getAuthor()));
    dto.setTitle(blog.getTitle());
    dto.setSubtitle(blog.getSubtitle());
    dto.setDescription(blog.getDescription());
    dto.setCode(blog.getCode());
    String image = blog.getImage();
    if (image == null && blog.getImages() != null && !blog.getImages().isEmpty()) {
      image = blog.getImages().get(0).getImageUrl();
    }
    dto.setImage(image);
    dto.setTags(serializeTags(blog.getTags()));
    return dto;
  }

  private List<BlogTag> buildTags(List<String> tags, Blog blog) {
    if (tags == null || tags.isEmpty()) {
      return List.of();
    }
    List<BlogTag> result = new ArrayList<>();
    for (String tagValue : tags) {
      if (tagValue == null) {
        continue;
      }
      String trimmed = tagValue.trim();
      if (trimmed.isEmpty()) {
        continue;
      }
      try {
        long tagId = Long.parseLong(trimmed);
        BlogTag tag = new BlogTag();
        tag.setBlog(blog);
        tag.setTagId(tagId);
        result.add(tag);
      } catch (NumberFormatException ignored) {
        BlogTag tag = new BlogTag();
        tag.setBlog(blog);
        tag.setTagName(trimmed);
        result.add(tag);
      }
    }
    return result;
  }

  private List<String> serializeTags(List<BlogTag> tags) {
    if (tags == null || tags.isEmpty()) {
      return List.of();
    }
    return tags.stream()
      .map(tag -> tag.getTagName() != null ? tag.getTagName() : String.valueOf(tag.getTagId()))
      .collect(Collectors.toList());
  }

  private List<BlogImage> buildImages(String imageUrl, Blog blog) {
    if (imageUrl == null || imageUrl.isBlank()) {
      return Collections.emptyList();
    }
    BlogImage image = new BlogImage();
    image.setBlog(blog);
    image.setImageUrl(imageUrl);
    image.setAltText(null);
    return List.of(image);
  }

  private User resolveAuthor(UserDto author) {
    if (author == null) {
      throw new IllegalArgumentException("author is required.");
    }
    if (author.getId() != null) {
      return userRepository.findById(author.getId())
        .orElseThrow(() -> new RuntimeException("Author not found."));
    }
    if (author.getEmail() != null && !author.getEmail().isBlank()) {
      return userRepository.findByEmail(author.getEmail())
        .orElseThrow(() -> new RuntimeException("Author not found."));
    }
    throw new IllegalArgumentException("author.id or author.email is required.");
  }

  private UserDto mapToDto(User user) {
    if (user == null) {
      return null;
    }
    UserDto dto = new UserDto();
    dto.setId(user.getId());
    dto.setUserName(user.getUserName());
    dto.setEmail(user.getEmail());
    dto.setRole(user.getRole());
    return dto;
  }
}
