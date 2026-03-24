package com.nickhazari.portfolio.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nickhazari.portfolio.entities.Blog;

public interface BlogRepository extends JpaRepository<Blog, UUID> {
}
