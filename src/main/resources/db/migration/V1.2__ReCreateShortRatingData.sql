DROP TABLE ratings;
CREATE TABLE ratings (
	rater_id INTEGER NOT NULL,
	movie_id INTEGER NOT NULL,
	rating NUMERIC(2, 1) NOT NULL,
	timestamp INTEGER NOT NULL,
	PRIMARY KEY (rater_id, movie_id)
);