package com.nickhazari.portfolio.dtos;

import java.time.LocalDateTime;
import java.util.UUID;

public record AdminCommentDto(
    UUID id,
    UUID blogId,
    String blogTitle,
    UUID userId,
    String userName,
    String userEmail,
    String content,
    LocalDateTime createdAt
) {}
