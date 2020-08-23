package yunxiao.movierecommender.dao;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import yunxiao.movierecommender.model.Movie;

@Repository("postgres-movie")
public class MovieDataAccessService implements MovieDao {
  
  private final JdbcTemplate jdbcTemplate;
  public static int size;
  public static int popularSize;
  public static Set<Integer> popularMovieIds;
  
  public MovieDataAccessService(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
    String sql = "SELECT reltuples::BIGINT AS estimate FROM pg_class WHERE relname='movies';";
    this.setSize(this.jdbcTemplate.queryForObject(sql, Integer.class));
    sql = "SELECT movie_id FROM popular_movies;";
    List<Integer> popularIds = new ArrayList<>();
    try {
      popularIds = jdbcTemplate.query(sql, (resultSet, i) -> {
        int movieId = resultSet.getInt("movie_id");
        return movieId;
      });
    } catch (EmptyResultDataAccessException e) {
      popularIds = null;
    }
    this.setPopularSize(popularIds.size());
    popularMovieIds = new HashSet<>();
    for (Integer id : popularIds) {
      popularMovieIds.add(id);
    }
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
    int bound = Integer.max(30, getSize() / 2);
    int[] randomRows = new Random().ints(2 * num, 0, bound)
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
  
public List<Movie> getRandomPopularMovies(int num) {
    
    // getSize() returns estimated size to avoid slow counting; /2 for safety
    int bound = Integer.max(30, getPopularSize());
    int[] randomRows = new Random().ints(2 * num, 0, bound)
                                       .distinct()
                                       .limit(num)
                                       .toArray();
    
    final String sql = "SELECT * FROM popular_movies LIMIT 1 OFFSET ?";
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
  
 
  public int getSize() {
    return size;
  }

  private void setSize(int size) {
    MovieDataAccessService.size = size;
  }

  public int getPopularSize() {
    return popularSize;
  }

  private void setPopularSize(int popularSize) {
    MovieDataAccessService.popularSize = popularSize;
  }
}
