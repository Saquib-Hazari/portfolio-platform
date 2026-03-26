package com.nickhazari.portfolio.dtos;

public record AdminStatsDto(
    long usersCount,
    long blogsCount,
    long commentsCount
) {}
