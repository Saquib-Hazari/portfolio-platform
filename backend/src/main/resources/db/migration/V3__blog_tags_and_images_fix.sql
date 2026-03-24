-- Align blog_images table name with JPA entity
DO $$
BEGIN
  IF EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name = 'blog_id')
     AND NOT EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name = 'blog_images') THEN
    ALTER TABLE blog_id RENAME TO blog_images;
  END IF;
END $$;
-- Rebuild blog_tags to support tag names or tag IDs
DROP TABLE IF EXISTS blog_tags;
CREATE TABLE IF NOT EXISTS blog_tags (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  blog_id UUID NOT NULL,
  tag_id INT,
  tag_name VARCHAR(50),
  CONSTRAINT fk_blog_tag_blog FOREIGN KEY (blog_id) REFERENCES blogs(id) ON DELETE CASCADE,
  CONSTRAINT fk_blog_tag_tag FOREIGN KEY (tag_id) REFERENCES tags(id) ON DELETE
  SET NULL
);
CREATE INDEX IF NOT EXISTS idx_blog_tag ON blog_tags(blog_id);
