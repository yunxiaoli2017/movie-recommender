DROP INDEX IF EXISTS idx_ratings_movie_id;

CREATE INDEX idx_ratings_movie_id
ON ratings(movie_id, rater_id, rating);