CREATE TABLE popular_movies (
	movie_id INTEGER NOT NULL PRIMARY KEY,
	imdb_id INTEGER,
	title VARCHAR(200) NOT NULL,
	genres VARCHAR(100),
	imdb_url VARCHAR(50),
	poster_url VARCHAR(200)
);

CREATE TABLE compact_ratings (
	rater_id INTEGER NOT NULL,
	movie_id INTEGER NOT NULL,
	rating NUMERIC(2, 1) NOT NULL,
	timestamp INTEGER NOT NULL,
	PRIMARY KEY (rater_id, movie_id)
);

CREATE INDEX idx_compact_ratings_movie_id
ON compact_ratings(movie_id, rater_id, rating);