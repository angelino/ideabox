CREATE TABLE IF NOT EXISTS ideas (
       id UUID NOT NULL PRIMARY KEY,
       title VARCHAR(255) NOT NULL,
       description VARCHAR(4000) NOT NULL,
       created_at TIMESTAMP NOT NULL DEFAULT NOW(),
       updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

ALTER TABLE IF EXISTS ideas ADD COLUMN IF NOT EXISTS rank SMALLINT DEFAULT 0;

CREATE INDEX IF NOT EXISTS ideas_rank ON ideas (rank);