package com.nickhazari.portfolio.dtos;

import java.util.UUID;

public record OrphanedImageDto(
    UUID id,
    String fileName,
    String contentType,
    String imageUrl
) {}
