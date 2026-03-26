package com.nickhazari.services;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.nickhazari.portfolio.dtos.BlogCreateRequest;
import com.nickhazari.portfolio.dtos.BlogDto;
import com.nickhazari.portfolio.dtos.BlogUpdateRequest;
import com.nickhazari.portfolio.dtos.UserDto;
import com.nickhazari.portfolio.entities.Blog;
import com.nickhazari.portfolio.entities.BlogImage;
import com.nickhazari.portfolio.entities.BlogTag;
import com.nickhazari.portfolio.entities.User;
import com.nickhazari.portfolio.exception.BadRequestException;
import com.nickhazari.portfolio.exception.NotFoundException;
import com.nickhazari.portfolio.repositories.BlogImageRepository;
import com.nickhazari.portfolio.repositories.BlogRepository;
import com.nickhazari.portfolio.repositories.UserRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class BlogService {
  private final BlogRepository blogRepository;
  private final BlogImageRepository blogImageRepository;
  private final UserRepository userRepository;

  public BlogDto createBlog(BlogCreateRequest blogDto) {
    Blog blog = mapToEntity(blogDto);
    blog.setCreatedAt(LocalDate.now());
    Blog saved = blogRepository.save(blog);
    return mapToDto(saved);
  }

  public List<BlogDto> getAllBlogs() {
    return blogRepository.findAll().stream().map(this::mapToDto).toList();
  }

  public BlogDto getBlog(UUID id) {
    Blog blog = blogRepository.findById(id)
      .orElseThrow(() -> new NotFoundException("Blog not found."));
    return mapToDto(blog);
  }

  public BlogDto updateBlog(UUID id, BlogUpdateRequest updated) {
    Blog blog = blogRepository.findById(id).orElseThrow(() -> new NotFoundException("Blog not found."));

    if (updated.getTitle() != null) {
      blog.setTitle(updated.getTitle());
    }
    if (updated.getSubtitle() != null) {
      blog.setSubtitle(updated.getSubtitle());
    }
    if (updated.getDescription() != null) {
      blog.setDescription(updated.getDescription());
    }
    if (updated.getCode() != null) {
      blog.setCode(updated.getCode());
    }
    if (updated.getImage() != null) {
      blog.setImage(updated.getImage());
      List<BlogImage> nextImages = buildImages(updated.getImage(), blog);
      if (blog.getImages() == null) {
        blog.setImages(new ArrayList<>());
      }
      blog.getImages().clear();
      blog.getImages().addAll(nextImages);
    }
    if (updated.getAuthor() != null) {
      blog.setAuthor(resolveAuthor(updated.getAuthor()));
    }
    if (updated.getTags() != null) {
      List<BlogTag> nextTags = buildTags(updated.getTags(), blog);
      if (blog.getTags() == null) {
        blog.setTags(new ArrayList<>());
      }
      blog.getTags().clear();
      blog.getTags().addAll(nextTags);
    }

    Blog saved = blogRepository.save(blog);
    return mapToDto(saved);
  }

  public void deleteBlog(UUID id) {
    if (!blogRepository.existsById(id)) {
      throw new NotFoundException("Blog not found.");
    }
    blogRepository.deleteById(id);
  }

  private Blog mapToEntity(BlogCreateRequest dto) {
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
      return new ArrayList<>();
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
      // Treat all incoming tags as names to avoid FK issues with tags table.
      BlogTag tag = new BlogTag();
      tag.setBlog(blog);
      tag.setTagName(trimmed);
      result.add(tag);
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
      return new ArrayList<>();
    }
    BlogImage existing = resolveStoredImage(imageUrl);
    if (existing != null) {
      existing.setBlog(blog);
      existing.setImageUrl(imageUrl);
      existing.setAltText(null);
      List<BlogImage> result = new ArrayList<>();
      result.add(existing);
      return result;
    }
    BlogImage image = new BlogImage();
    image.setBlog(blog);
    image.setImageUrl(imageUrl);
    image.setAltText(null);
    List<BlogImage> result = new ArrayList<>();
    result.add(image);
    return result;
  }

  private BlogImage resolveStoredImage(String imageUrl) {
    if (imageUrl == null || imageUrl.isBlank()) {
      return null;
    }
    String trimmed = imageUrl.trim();
    String marker = "/api/uploads/images/";
    int idx = trimmed.indexOf(marker);
    if (idx >= 0) {
      String idPart = trimmed.substring(idx + marker.length());
      return fetchImageById(idPart);
    }
    if (trimmed.startsWith("uploads/") || trimmed.startsWith("/uploads/")) {
      return null;
    }
    if (trimmed.startsWith("images/") || trimmed.startsWith("/images/")) {
      String idPart = trimmed.substring(trimmed.lastIndexOf('/') + 1);
      return fetchImageById(idPart);
    }
    return null;
  }

  private BlogImage fetchImageById(String idPart) {
    try {
      return blogImageRepository.findById(UUID.fromString(idPart)).orElse(null);
    } catch (IllegalArgumentException ex) {
      return null;
    }
  }

  private User resolveAuthor(UserDto author) {
    if (author == null) {
      throw new BadRequestException("author is required.");
    }
    if (author.getId() != null) {
      return userRepository.findById(author.getId())
        .orElseThrow(() -> new NotFoundException("Author not found."));
    }
    if (author.getEmail() != null && !author.getEmail().isBlank()) {
      return userRepository.findByEmail(author.getEmail())
        .orElseThrow(() -> new NotFoundException("Author not found."));
    }
    throw new BadRequestException("author.id or author.email is required.");
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
