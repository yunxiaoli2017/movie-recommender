CREATE TABLE ratings (
	movie_id INTEGER NOT NULL,
	rater_id INTEGER NOT NULL,
	rating NUMERIC(2, 1) NOT NULL,
	timestamp INTEGER NOT NULL
);