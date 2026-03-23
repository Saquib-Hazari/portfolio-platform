package com.nickhazari.portfolio.entities;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "users")
public class User {
  @Id
  @UuidGenerator
  @Column(name = "id")
  private UUID id;

  @Column(name = "username")
  private String userName;

  @Column(name = "email")
  private String email;

  @Column(name = "password")
  private String password;

  @Column(name = "role")
  private String role;

  @Column(name = "profile_image")
  private String profileImage;

  @Column(name = "bio")
  private String bio;

  @Column(name = "created_at")
  private LocalDateTime createdAt;

  @Column(name = "updated_at")
  private LocalDateTime updatedAt;

  @Override
  public String toString() {
    return "User{" +
        "id=" + id +
        ", username='" + userName + '\'' +
        ", email='" + email + '\'' +
        '}';
  }

}
