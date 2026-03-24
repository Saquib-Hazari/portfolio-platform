package com.nickhazari.services;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class LocalStorageService {
  private final Path uploadDir;

  public LocalStorageService(@Value("${UPLOAD_DIR:uploads}") String uploadDir) {
    this.uploadDir = Paths.get(uploadDir).toAbsolutePath().normalize();
  }

  public String saveImage(MultipartFile file) {
    try {
      Files.createDirectories(uploadDir);
      String originalName = file.getOriginalFilename();
      String extension = "";
      if (originalName != null && originalName.contains(".")) {
        extension = originalName.substring(originalName.lastIndexOf('.'));
      }
      String filename = UUID.randomUUID() + extension;
      Path target = uploadDir.resolve(filename);
      Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
      return filename;
    } catch (IOException ex) {
      throw new RuntimeException("Failed to store file.", ex);
    }
  }
}
