package com.nickhazari.portfolio.tools;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import com.nickhazari.portfolio.entities.Blog;
import com.nickhazari.portfolio.entities.BlogImage;
import com.nickhazari.portfolio.repositories.BlogImageRepository;
import com.nickhazari.portfolio.repositories.BlogRepository;

@Component
@ConditionalOnProperty(prefix = "image.import", name = "enabled", havingValue = "true")
public class ImageImportRunner implements ApplicationRunner {
  private static final Logger log = LoggerFactory.getLogger(ImageImportRunner.class);

  private final BlogImageRepository blogImageRepository;
  private final BlogRepository blogRepository;
  private final JdbcTemplate jdbcTemplate;
  private final String uploadDir;

  public ImageImportRunner(
      BlogImageRepository blogImageRepository,
      BlogRepository blogRepository,
      JdbcTemplate jdbcTemplate,
      @Value("${UPLOAD_DIR:uploads}") String uploadDir) {
    this.blogImageRepository = blogImageRepository;
    this.blogRepository = blogRepository;
    this.jdbcTemplate = jdbcTemplate;
    this.uploadDir = uploadDir;
  }

  @Override
  public void run(ApplicationArguments args) throws Exception {
    if (hasAlreadyRun()) {
      log.info("Image import skipped. Previously completed.");
      return;
    }

    Path dir = Paths.get(uploadDir).toAbsolutePath().normalize();
    if (!Files.isDirectory(dir)) {
      log.warn("Image import skipped. Upload directory not found: {}", dir);
      return;
    }

    Map<String, BlogImage> imported = new HashMap<>();
    try (var paths = Files.list(dir)) {
      paths.filter(Files::isRegularFile).forEach(path -> {
        String fileName = path.getFileName().toString();
        BlogImage existing = blogImageRepository.findFirstByFileName(fileName);
        if (existing != null) {
          if (existing.getImageUrl() == null || existing.getImageUrl().isBlank()) {
            existing.setImageUrl("/api/uploads/images/" + existing.getId());
            existing = blogImageRepository.save(existing);
          }
          imported.put(fileName, existing);
          return;
        }
        BlogImage image = new BlogImage();
        image.setFileName(fileName);
        image.setContentType(detectContentType(path));
        try {
          image.setImageData(Files.readAllBytes(path));
        } catch (IOException ex) {
          log.warn("Failed to read image {}: {}", path, ex.getMessage());
          return;
        }
        BlogImage saved = blogImageRepository.save(image);
        saved.setImageUrl("/api/uploads/images/" + saved.getId());
        saved = blogImageRepository.save(saved);
        imported.put(fileName, saved);
      });
    }

    if (imported.isEmpty()) {
      log.info("Image import complete. No new files found.");
      markCompleted();
      return;
    }

    List<Blog> blogs = blogRepository.findAll();
    int linked = 0;
    for (Blog blog : blogs) {
      String imageValue = blog.getImage();
      String fileName = extractFileName(imageValue);
      if (fileName == null) {
        continue;
      }
      BlogImage image = imported.get(fileName);
      if (image == null) {
        continue;
      }
      String newUrl = "/api/uploads/images/" + image.getId();
      if (!newUrl.equals(blog.getImage())) {
        blog.setImage(newUrl);
      }
      if (blog.getImages() == null || blog.getImages().isEmpty()) {
        blog.setImages(List.of(image));
      } else if (!blog.getImages().contains(image)) {
        blog.getImages().add(image);
      }
      image.setBlog(blog);
      blogRepository.save(blog);
      blogImageRepository.save(image);
      linked++;
    }

    log.info("Image import complete. Imported: {}, linked to blogs: {}", imported.size(), linked);
    markCompleted();
  }

  private String extractFileName(String value) {
    if (value == null || value.isBlank()) {
      return null;
    }
    String trimmed = value.trim();
    if (trimmed.startsWith("http://") || trimmed.startsWith("https://")) {
      return null;
    }
    if (trimmed.contains("/api/uploads/images/")) {
      return null;
    }
    int slash = trimmed.lastIndexOf('/');
    if (slash >= 0) {
      return trimmed.substring(slash + 1);
    }
    return trimmed;
  }

  private String detectContentType(Path path) {
    try {
      String type = Files.probeContentType(path);
      if (type != null && !type.isBlank()) {
        return type;
      }
    } catch (IOException ignored) {
      // fall through
    }
    return "application/octet-stream";
  }

  private boolean hasAlreadyRun() {
    Integer count = jdbcTemplate.queryForObject(
        "SELECT COUNT(*) FROM image_import_log",
        Integer.class);
    return count != null && count > 0;
  }

  private void markCompleted() {
    jdbcTemplate.update("INSERT INTO image_import_log DEFAULT VALUES");
  }
}
