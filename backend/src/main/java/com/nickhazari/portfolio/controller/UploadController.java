package com.nickhazari.portfolio.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.nickhazari.portfolio.dtos.UploadResponse;
import com.nickhazari.services.LocalStorageService;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/uploads")
@CrossOrigin(origins = "*")
@AllArgsConstructor
public class UploadController {
  private final LocalStorageService localStorageService;

  @PostMapping("/images")
  public ResponseEntity<UploadResponse> uploadImage(@RequestParam("file") MultipartFile file) {
    String filename = localStorageService.saveImage(file);
    String url = ServletUriComponentsBuilder.fromCurrentContextPath()
      .path("/uploads/")
      .path(filename)
      .toUriString();
    return ResponseEntity.ok(new UploadResponse(url, filename));
  }
}
