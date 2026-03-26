package com.nickhazari.portfolio.dtos;

import java.time.LocalDateTime;
import java.util.UUID;

public class CommentDto {
  private UUID id;
  private UUID userId;
  private String userName;
  private String userEmail;
  private String content;
  private LocalDateTime createdAt;
  private long likes;
  private long dislikes;
  private long hearts;
  private boolean likedByMe;
  private boolean dislikedByMe;
  private boolean heartedByMe;

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public UUID getUserId() {
    return userId;
  }

  public void setUserId(UUID userId) {
    this.userId = userId;
  }

  public String getUserName() {
    return userName;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

  public String getUserEmail() {
    return userEmail;
  }

  public void setUserEmail(String userEmail) {
    this.userEmail = userEmail;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }

  public long getLikes() {
    return likes;
  }

  public void setLikes(long likes) {
    this.likes = likes;
  }

  public long getDislikes() {
    return dislikes;
  }

  public void setDislikes(long dislikes) {
    this.dislikes = dislikes;
  }

  public long getHearts() {
    return hearts;
  }

  public void setHearts(long hearts) {
    this.hearts = hearts;
  }

  public boolean isLikedByMe() {
    return likedByMe;
  }

  public void setLikedByMe(boolean likedByMe) {
    this.likedByMe = likedByMe;
  }

  public boolean isDislikedByMe() {
    return dislikedByMe;
  }

  public void setDislikedByMe(boolean dislikedByMe) {
    this.dislikedByMe = dislikedByMe;
  }

  public boolean isHeartedByMe() {
    return heartedByMe;
  }

  public void setHeartedByMe(boolean heartedByMe) {
    this.heartedByMe = heartedByMe;
  }
}
