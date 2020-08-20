package yunxiao.movierecommender.service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import yunxiao.movierecommender.dao.MovieDao;
import yunxiao.movierecommender.model.Movie;

@Service
public class MovieService {
  private final MovieDao movieDao;
  
  @Autowired
  public MovieService(@Qualifier("postgres-movie") MovieDao movieDao) {
    this.movieDao = movieDao;
  }
  
  public Optional<Movie> getMovieById(int id) {
    return movieDao.getMovieById(id);
  }
  
  public List<Movie> getRandomMovies(int num) {
    return movieDao.getRandomMovies(num);
  }
  
  public List<Movie> getRandomMovies(int num, double percentage) {
    return movieDao.getRandomMovies(num, percentage);
  }
  
  public List<Movie> getRecommendedMovies(List<Integer> movieIds) {
    List<Movie> movies = new ArrayList<>();
    Movie movie = new Movie();
    for (Integer id : movieIds) {
      try {
        movie = getMovieById(id).get();
      } catch (NoSuchElementException e) {
        movie = null;
      }
      if (movie != null) {
        movies.add(movie);
      }
    }
    return movies;
  }
}
