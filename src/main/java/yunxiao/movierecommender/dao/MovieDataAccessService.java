package yunxiao.movierecommender.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import yunxiao.movierecommender.model.Movie;

@Repository("postgres-movie")
public class MovieDataAccessService implements MovieDao {
  
  private final JdbcTemplate jdbcTemplate;
  private int size;
  
  public MovieDataAccessService(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
    final String sql = "SELECT reltuples::BIGINT AS estimate FROM pg_class WHERE relname='movies';";
    this.setSize(this.jdbcTemplate.queryForObject(sql, Integer.class));
  }
  
  @Override
  public Optional<Movie> getMovieById(int id) {
    final String sql = "SELECT * FROM movies WHERE movie_id = ?";
    Movie movie = new Movie();
    try {
      movie = jdbcTemplate.queryForObject(sql, new Object[] {id}, (resultSet, i) -> {
        int movieId = resultSet.getInt("movie_id");
        int imdbId = resultSet.getInt("imdb_id");
        String title = resultSet.getString("title");
        String genres = resultSet.getString("genres");
        String imdbUrl = resultSet.getString("imdb_url");
        String posterUrl = resultSet.getString("poster_url");
        return new Movie(movieId, imdbId, title, genres, imdbUrl, posterUrl);
      });
    } catch (EmptyResultDataAccessException e) {
      movie = null;
    }
    return Optional.ofNullable(movie);
  }


  @Override
  public List<Movie> getRandomMovies(int num) {
    
    // getSize() returns estimated size to avoid slow counting; /2 for safety
    int[] randomRows = new Random().ints(2 * num, 0, getSize() / 2)
                                       .distinct()
                                       .limit(num)
                                       .toArray();
    
    final String sql = "SELECT * FROM movies LIMIT 1 OFFSET ?";
    List<Movie> randomMovies = new ArrayList<>();
    for (int row : randomRows) {
      Movie movie = new Movie();
      try {
        movie = jdbcTemplate.queryForObject(sql, new Object[] {row}, (resultSet, i) -> {
          int movieId = resultSet.getInt("movie_id");
          int imdbId = resultSet.getInt("imdb_id");
          String title = resultSet.getString("title");
          String genres = resultSet.getString("genres");
          String imdbUrl = resultSet.getString("imdb_url");
          String posterUrl = resultSet.getString("poster_url");
          return new Movie(movieId, imdbId, title, genres, imdbUrl, posterUrl);
        });
      } catch (EmptyResultDataAccessException e) {
        movie = new Movie();
      }
      if (!movie.isBlank()) {
        randomMovies.add(movie);
      }
    }
    return randomMovies;
  }
  
  @Override
  public List<Movie> getRandomMovies(int num, double percentage) {
    final String sql = String.format("SELECT * FROM movies TABLESAMPLE SYSTEM(%f) LIMIT 1", percentage);
    int currentAttempts = 0;
    int maxAttempts = 100 * num;
    List<Movie> randomMovies = new ArrayList<Movie>();
    
    while (randomMovies.size() < num && currentAttempts < maxAttempts) {
      try {
        Movie movie = jdbcTemplate.queryForObject(sql, (resultSet, i) -> {
          int movieId = resultSet.getInt("movie_id");
          int imdbId = resultSet.getInt("imdb_id");
          String title = resultSet.getString("title");
          String genres = resultSet.getString("genres");
          String imdbUrl = resultSet.getString("imdb_url");
          String posterUrl = resultSet.getString("poster_url");
          return new Movie(movieId, imdbId, title, genres, imdbUrl, posterUrl);
        });
        randomMovies.add(movie);
      } catch (EmptyResultDataAccessException e) {
        
      }
      currentAttempts++;
    }
    return randomMovies;
  }

  public int getSize() {
    return size;
  }

  public void setSize(int size) {
    this.size = size;
  }
}
