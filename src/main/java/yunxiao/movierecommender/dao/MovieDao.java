package yunxiao.movierecommender.dao;

import java.util.List;
import java.util.Optional;

import yunxiao.movierecommender.model.Movie;

public interface MovieDao {
  
  Optional<Movie> getMovieById(int id);
  
  List<Movie> getRandomMovies(int num);
  
  List<Movie> getRandomMovies(int num, double percentage);
}
