package com.nickhazari.portfolio.entities;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "blogs")
public class Blog {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "id")
  private UUID id;

  @Column(name = "title", columnDefinition = "TEXT")
  private String title;

  @ManyToOne(optional = false)
  @JoinColumn(name = "author_id", nullable = false)
  private User author;

  @Column(name = "description", columnDefinition = "TEXT")
  private String subtitle;

  @Column(name = "body", columnDefinition = "TEXT")
  private String description;

  @Column(name = "code_snippet", columnDefinition = "TEXT")
  private String code;

  @Column(name = "cover_image", columnDefinition = "TEXT")
  private String image;

  @Column(name = "publish")
  private boolean publish;

  @Column(name = "created_at")
  private LocalDate createdAt;

  @OneToMany(mappedBy = "blog", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<BlogTag> tags;

  @OneToMany(mappedBy = "blog", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<BlogImage> images;
}
