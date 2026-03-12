-- Enable UUID extension if not already
CREATE EXTENSION IF NOT EXISTS "pgcrypto";
-- Users Table
DROP TABLE IF EXISTS users;
CREATE TABLE IF NOT EXISTS users (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  username VARCHAR(50) NOT NULL,
  email VARCHAR(120) NOT NULL,
  password VARCHAR(255) NOT NULL,
  profile_image TEXT,
  bio TEXT,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
-- Blogs Table
DROP TABLE IF EXISTS blogs;
CREATE TABLE IF NOT EXISTS blogs (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  author_id UUID NOT NULL,
  title VARCHAR(200) NOT NULL,
  description TEXT,
  body TEXT NOT NULL,
  code_snippet TEXT,
  cover_image TEXT,
  publish BOOLEAN DEFAULT FALSE,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_blog_author FOREIGN KEY (author_id) REFERENCES users(id) ON DELETE CASCADE
);
-- Blog Images Table
DROP TABLE IF EXISTS blog_images;
CREATE TABLE IF NOT EXISTS blog_images (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  blog_id UUID NOT NULL,
  image_url TEXT NOT NULL,
  alt_text VARCHAR(255),
  CONSTRAINT fk_blog_image FOREIGN KEY (blog_id) REFERENCES blogs(id) ON DELETE CASCADE
);
-- Comments Table
DROP TABLE IF EXISTS comments;
CREATE TABLE IF NOT EXISTS comments (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  blog_id UUID NOT NULL,
  user_id UUID NOT NULL,
  content TEXT NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_comment_blog FOREIGN KEY (blog_id) REFERENCES blogs(id) ON DELETE CASCADE,
  CONSTRAINT fk_comment_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);
-- Tags Table
DROP TABLE IF EXISTS tags;
CREATE TABLE IF NOT EXISTS tags (
  id SERIAL PRIMARY KEY,
  name VARCHAR(50) UNIQUE NOT NULL
);
-- Blog-Tags Join Table
DROP TABLE IF EXISTS blog_tags;
CREATE TABLE IF NOT EXISTS blog_tags (
  blog_id UUID NOT NULL,
  tag_id INT NOT NULL,
  PRIMARY KEY(blog_id, tag_id),
  CONSTRAINT fk_blog_tag_blog FOREIGN KEY (blog_id) REFERENCES blogs(id) ON DELETE CASCADE,
  CONSTRAINT fk_blog_tag_tag FOREIGN KEY (tag_id) REFERENCES tags(id) ON DELETE CASCADE
);
-- Indexes
CREATE INDEX idx_blog_author ON blogs(author_id);
CREATE INDEX idx_comment_blog ON comments(blog_id);
CREATE INDEX idx_comment_user ON comments(user_id);
CREATE INDEX idx_blog_tag ON blog_tags(blog_id, tag_id);