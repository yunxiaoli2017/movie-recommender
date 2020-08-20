package yunxiao.movierecommender.dao;

import java.util.List;
import java.util.Map;

import yunxiao.movierecommender.model.Rating;

public interface RatingDao {
  
  List<Rating> getRatingByRaterId(int raterId);
  List<Rating> getRatingByMovieId(int movieId);
  List<Integer> getAllRaterIds();
  List<Rating> getRatingByRaterIdAndMovieIds(Integer raterId, List<Integer> movieIds);
  Rating getRatingByRaterIdAndMovieId(Integer raterId, Integer movieId);
  Map<Integer, Double> getSimilarityMap(List<Rating> ratings);
}
