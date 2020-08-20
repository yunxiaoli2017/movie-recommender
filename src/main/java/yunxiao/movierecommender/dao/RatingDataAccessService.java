package yunxiao.movierecommender.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import yunxiao.movierecommender.model.Rater;
import yunxiao.movierecommender.model.Rating;

@Repository("postgres-rating")
public class RatingDataAccessService implements RatingDao {

  private final JdbcTemplate jdbcTemplate;
  
  public RatingDataAccessService(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }
  
  @Override
  public List<Rating> getRatingByRaterId(int raterId) {
    final String sql = "SELECT * FROM ratings WHERE rater_id = ?";
    List<Rating> ratings = new ArrayList<>();
    try {
      ratings = jdbcTemplate.query(sql, new Object[] {raterId}, (resultSet, i) -> {
        int movieId = resultSet.getInt("movie_id");
        double rating = resultSet.getDouble("rating");
        int timestamp = resultSet.getInt("timestamp");
        return new Rating(raterId, movieId, rating, timestamp);
      });
    } catch (EmptyResultDataAccessException e) {
      ratings = null;
    }
    return ratings;
  }

  @Override
  public List<Rating> getRatingByMovieId(int movieId) {
    final String sql = "SELECT * FROM ratings WHERE movie_id = ?";
    List<Rating> ratings = new ArrayList<>();
    try {
      ratings = jdbcTemplate.query(sql, new Object[] {movieId}, (resultSet, i) -> {
        int raterId = resultSet.getInt("rater_id");
        double rating = resultSet.getDouble("rating");
        int timestamp = resultSet.getInt("timestamp");
        return new Rating(raterId, movieId, rating, timestamp);
      });
    } catch (EmptyResultDataAccessException e) {
      ratings = null;
    }
    return ratings;
  }
  
  @Override
  public List<Integer> getAllRaterIds() {
    final String sql = "SELECT DISTINCT rater_id FROM ratings";
    List<Integer> raterIds = new ArrayList<>();
    try {
      raterIds = jdbcTemplate.query(sql, (resultSet, i) -> {
        int raterId = resultSet.getInt("rater_id");
        return raterId;
      });
    } catch (EmptyResultDataAccessException e) {
      raterIds = null;
    }
    return raterIds;
  }

//  @Override
//  public List<Rating> getRatingByRaterIdAndMovieIds(Integer raterId, List<Integer> movieIds) {
//    final String sql = "SELECT * FROM ratings WHERE rater_id = ? AND movie_id = ANY (string_to_array(?, ',')";
//    StringBuilder idString = new StringBuilder();
//    for (Integer movieId: movieIds) {
//      idString.append(movieId.toString());
//      idString.append(",");
//    }
//    idString.deleteCharAt(idString.length() - 1);
//    List<Rating> ratings = new ArrayList<>();
//    try {
//      ratings = jdbcTemplate.query(sql, 
//                                   new Object[] {raterId, idString.toString()}, 
//                                   new int[] {Types.INTEGER, Types.VARCHAR}, 
//                                   (resultSet, i) -> {
//                                      int movieId = resultSet.getInt("movie_id");
//                                      double rating = resultSet.getDouble("rating");
//                                      int timestamp = resultSet.getInt("timestamp");
//                                      return new Rating(raterId, movieId, rating, timestamp);
//                                    });
//    } catch (EmptyResultDataAccessException e) {
//      ratings = null;
//    }
//    return ratings;
//  }
  
  @Override
  public List<Rating> getRatingByRaterIdAndMovieIds(Integer raterId, List<Integer> movieIds) {
    List<Rating> ratings = new ArrayList<>();
    for (Integer movieId : movieIds) {
      Rating rating = getRatingByRaterIdAndMovieId(raterId, movieId);
      if (rating != null) {
        ratings.add(rating);
      }
    }
    return ratings;
  }
  
  
  @Override
  public Rating getRatingByRaterIdAndMovieId(Integer raterId, Integer movieId) {
    final String sql = "SELECT * FROM ratings WHERE rater_id = ? AND movie_id = ?";
    Rating rating = new Rating();
    try {
      rating = jdbcTemplate.queryForObject(sql, new Object[] {raterId, movieId}, (resultSet, i) -> {
        double r = resultSet.getDouble("rating");
        int timestamp = resultSet.getInt("timestamp");
        return new Rating(raterId, movieId, r, timestamp);
      });
    } catch (EmptyResultDataAccessException e) {
      rating = null;
    }
    return rating;
  }

  @Override
  public Map<Integer, Double> getSimilarityMap(List<Rating> ratings) {
    Map<Integer, Double> similarities = new HashMap<Integer, Double>();
    final String sql = "SELECT rater_id, rating FROM ratings WHERE movie_id = ?";
    for (Rating r : ratings) {
      List<Rater> raters = new ArrayList<>();
      int movieId = r.getMovieId();
      try {
        raters = jdbcTemplate.query(sql, 
                                     new Object[] {movieId},
                                     (resultSet, i) -> {
                                        int raterId = resultSet.getInt("rater_id");
                                        double rating = resultSet.getDouble("rating");
                                        double similarity = (rating - 3) * (r.getRating() - 3);
                                        return new Rater(raterId, similarity);
                                      });
      } catch (EmptyResultDataAccessException e) {
        raters = null;
      }
      if (raters != null && !raters.isEmpty()) {
        for (Rater rater : raters) {
          similarities.put(rater.getId(), similarities.getOrDefault(rater.getId(), (double) 0) + rater.getSimilarity());
        }
      }
//      if (similarities.size() > 100000) {
//        System.out.print("similarities exceeds size 100,000");
//        break;
//      }
    }
//    System.out.print("similarityMap has size of ");
//    System.out.print(similarities.size());
//    System.out.print("\n");
    return similarities;
  }
}
