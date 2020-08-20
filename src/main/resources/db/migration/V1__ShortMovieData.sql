CREATE TABLE movies (
	movie_id INTEGER NOT NULL PRIMARY KEY,
	imdb_id INTEGER,
	title VARCHAR(200) NOT NULL,
	genres VARCHAR(100),
	imdb_url VARCHAR(50),
	poster_url VARCHAR(200)
);