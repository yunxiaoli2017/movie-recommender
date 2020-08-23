package yunxiao.movierecommender.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.PriorityQueue;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import yunxiao.movierecommender.dao.MovieDataAccessService;
import yunxiao.movierecommender.dao.RatingDao;
import yunxiao.movierecommender.model.Rater;
import yunxiao.movierecommender.model.Rating;

@Service
public class RatingService {
  
  private final RatingDao ratingDao;
  static public int MAX_RECOMMENDATIONS = 10;
  static public int MAX_RECOMMENDING_RATERS = 100;
  static public final List<Double> POSSIBLE_RATINGS = Collections.unmodifiableList(Arrays.asList((double) 1, (double) 2, (double) 3, (double) 4, (double) 5));
  
  @Autowired
  public RatingService(@Qualifier("postgres-rating") RatingDao ratingDao) {
    this.ratingDao = ratingDao;
  }
  
  public List<Rating> getRatingByRaterId(int raterId) {
    return ratingDao.getRatingByRaterId(raterId);
  }
  
  public List<Rating> getRatingByMovieId(int movieId) {
    return ratingDao.getRatingByMovieId(movieId);
  }
  
  public List<Integer> getRecommendedMovieIds(List<Rating> ratings) {
    Set<Integer> ratedMovieIds = getRatedMovieIds(ratings);
    Map<Integer, Double> similarities = ratingDao.getSimilarityMap(ratings);
    List<Rater> raters = getTopRaters(similarities);
    Map<Integer, Double> movieScores = getMovieScores(raters, ratedMovieIds);
    List<Map<Integer, Double>> dividedMovieScores = divideByPopularity(movieScores);
    Map<Integer, Double> popularMovieScores = dividedMovieScores.get(0);
    Map<Integer, Double> unpopularMovieScores = dividedMovieScores.get(1);
    int numMovie = Integer.min(MAX_RECOMMENDATIONS, unpopularMovieScores.size());
    List<Integer> topUnpopularMovies = getTopMovieIds(unpopularMovieScores, numMovie);
    numMovie = Integer.min(2 * MAX_RECOMMENDATIONS - topUnpopularMovies.size(), popularMovieScores.size());
    List<Integer> topPopularMovies = getTopMovieIds(popularMovieScores, numMovie);
    List<Integer> topMovies = weaveTwoLists(topPopularMovies, topUnpopularMovies);
    return topMovies;
  }
  
  private List<Integer> weaveTwoLists(List<Integer> topPopularMovies, List<Integer> topUnpopularMovies) {
    List<Integer> topMovies = new ArrayList<>();
    for (int i = 0; i < Integer.max(topUnpopularMovies.size(), topPopularMovies.size()); i++) {
      if (i < topPopularMovies.size()) {
        topMovies.add(topPopularMovies.get(i));
      }
      if (i < topUnpopularMovies.size()) {
        topMovies.add(topUnpopularMovies.get(i));
      }
    }
    return topMovies;
  }

  private List<Map<Integer, Double>> divideByPopularity(Map<Integer, Double> movieScores) {
    List<Map<Integer, Double>> dividedMovieScores = new ArrayList<>();
    Map<Integer, Double> popularMovieScores = new HashMap<>();
    Map<Integer, Double> unpopularMovieScores = new HashMap<>();
    for (Map.Entry<Integer, Double> entry : movieScores.entrySet()) {
      if (MovieDataAccessService.popularMovieIds.contains(entry.getKey())) {
        popularMovieScores.put(entry.getKey(), entry.getValue());
      } else {
        unpopularMovieScores.put(entry.getKey(), entry.getValue());
      }
    }
    dividedMovieScores.add(popularMovieScores);
    dividedMovieScores.add(unpopularMovieScores);
    return dividedMovieScores;
  }


  private List<Rater> getTopRaters(Map<Integer, Double> similarities) {
    int numRater = Integer.min(MAX_RECOMMENDING_RATERS, similarities.size());
    Comparator<Entry<Integer, Double>> similarityComparator = new Comparator<Entry<Integer, Double>>() {
        @Override
        public int compare(Entry<Integer, Double> e0, Entry<Integer, Double> e1)
        {
            Double v0 = e0.getValue();
            Double v1 = e1.getValue();
            return v0.compareTo(v1);
        }
    };
    PriorityQueue<Entry<Integer, Double>> topSimilarities = new PriorityQueue<Entry<Integer, Double>>(numRater, similarityComparator);
    for (Entry<Integer, Double> entry : similarities.entrySet()) {
      topSimilarities.offer(entry);
      if (topSimilarities.size() > numRater) {
        topSimilarities.poll();
      }
    }
    List<Rater> topRaters = new ArrayList<Rater>();
    while (topSimilarities.size() > 0) {
      Entry<Integer, Double> entry = topSimilarities.poll();
      topRaters.add(new Rater(entry.getKey(), entry.getValue()));
    }
    return topRaters;
  }

  private Set<Integer> getRatedMovieIds(List<Rating> ratings) {
    Set<Integer> ids = new HashSet<>();
    for (Rating r : ratings) {
      ids.add(r.getMovieId());
    }
    return ids;
  }

  private List<Integer> getTopMovieIds(Map<Integer, Double> scores, int numMovie) {
    numMovie = Integer.min(numMovie, scores.size());
    if (numMovie < 1) {
      return new ArrayList<Integer>();
    }
    Comparator<Entry<Integer, Double>> scoreComparator = new Comparator<Entry<Integer, Double>>() {
        @Override
        public int compare(Entry<Integer, Double> e0, Entry<Integer, Double> e1)
        {
            Double v0 = e0.getValue();
            Double v1 = e1.getValue();
            return v0.compareTo(v1);
        }
    };
    PriorityQueue<Entry<Integer, Double>> topScores = new PriorityQueue<Entry<Integer, Double>>(numMovie, scoreComparator);
    for (Entry<Integer, Double> entry : scores.entrySet()) {
      topScores.offer(entry);
      if (topScores.size() > numMovie) {
        topScores.poll();
      }
    }
    List<Integer> topMovieIds = new ArrayList<Integer>();
    while (topScores.size() > 0) {
      topMovieIds.add(topScores.poll().getKey());
    }
    return topMovieIds;
  }

  private Map<Integer, Double> getMovieScores(List<Rater> raters, Set<Integer> ratedMovieIds) {
    Map<Integer, Double> scores = new HashMap<>();
    for (Rater rater : raters) {
      List<Rating> ratings = getRatingsByRaterId(rater.getId());
      for (Rating rating: ratings) {
        if (!ratedMovieIds.contains(rating.getMovieId())) {
          scores.put(rating.getMovieId(), scores.getOrDefault(rating.getMovieId(), (double) 0) + rating.getRating() * rater.getSimilarity());
        }
//        if (scores.size() > 1000000) {
//          System.out.print("scores size exceeds 1,000,000");
//          break;
//        }
      }
    }
    return scores;
  }

  private List<Rating> getRatingsByRaterId(int id) {
    return ratingDao.getRatingByRaterId(id);
  }

//  private List<Rater> getSimilarRaters(List<Rating> ratings) {
//    List<Integer> raterIds = getAllRaterIds();
//    List<Rater> raters = new ArrayList<>();
//    for (Integer id: raterIds) {
//      Double similarity = getSimilarityWithRatings(id, ratings);
//      if (similarity > 0.1) {
//        raters.add(new Rater(id, similarity));
//      }
//    }
//    return raters;
//  }

//  private Double getSimilarityWithRatings(Integer raterId, List<Rating> ratings) {
//    List<Integer> movieIds = new ArrayList<>();
//    for (Rating rating: ratings) {
//      movieIds.add(rating.getMovieId());
//    }
//    List<Rating> raterRatings = ratingDao.getRatingByRaterIdAndMovieIds(raterId, movieIds);
//    if (raterRatings == null || raterRatings.isEmpty()) {
//      return (double) 0;
//    }
//    return getSimilarityBetweenRatings(raterRatings, ratings);
//  }

//  private Double getSimilarityBetweenRatings(List<Rating> raterRatings, List<Rating> ratings) {
//    Double similarity = (double) 0;
//    normalizeRatings(raterRatings);
//    subtractThreeFromRatings(ratings);
//    for (Rating r1 : ratings) {
//      int movieId = r1.getMovieId();
//      for (Rating r2: raterRatings) {
//        if (r2.getMovieId() == movieId) {
//          similarity += r1.getRating() * r2.getRating();
//        }
//      }
//    }
//    return similarity;
//  }

//  private void subtractThreeFromRatings(List<Rating> ratings) {
//    for (Rating r : ratings) {
//      r.setRating(r.getRating() - 3);
//    }
//  }

//  private void normalizeRatings(List<Rating> ratings) {
//    Double meanRating = (double) 0;
//    for (Rating r : ratings) {
//      meanRating += r.getRating() / ratings.size();
//    }
//    for (Rating r : ratings) {
//      r.setRating(r.getRating() - meanRating);
//    }
//  }

//  private List<Integer> getAllRaterIds() {
//    return ratingDao.getAllRaterIds();
//  }

  public List<Rating> filterZeroRating(List<Rating> ratings) {
    List<Rating> nonZeroRatings = new ArrayList<>();
    for (Rating r : ratings) {
      if (r.getRating() > 0.1) {
        nonZeroRatings.add(r);
      }
    }
    return nonZeroRatings;
  }
  
//  public List<Rating> filterZeroAndThreeRating(List<Rating> ratings) {
//    List<Rating> filteredRatings = new ArrayList<>();
//    for (Rating r : ratings) {
//      if (r.getRating() > 0.1 && (r.getRating() < 2.9 || r.getRating() > 3.1)) {
//        filteredRatings.add(r);
//      }
//    }
//    return filteredRatings;
//  }

  public void decouplingNormalization(List<Rating> ratings) {
    Map<Double, Double> percentageOfRatingLowerThan = new HashMap<>();
    Map<Double, Double> percentageOfRatingEqualTo = new HashMap<>();
    int numRatings = ratings.size();
    for (Rating r : ratings) {
      percentageOfRatingEqualTo.put(r.getRating(), percentageOfRatingEqualTo.getOrDefault(r.getRating(), (double) 0) + (double) 1 / numRatings);
    }
    for (int i = 1; i < POSSIBLE_RATINGS.size(); i++) {
      Double currentR = POSSIBLE_RATINGS.get(i);
      Double prevR = POSSIBLE_RATINGS.get(i - 1);
      percentageOfRatingLowerThan.put(currentR, percentageOfRatingLowerThan.getOrDefault(prevR, (double) 0) + percentageOfRatingEqualTo.getOrDefault(prevR, (double) 0));
    }
    for (Rating r : ratings) {
      double unnormalized = r.getRating();
      r.setRating(percentageOfRatingLowerThan.getOrDefault(unnormalized, (double) 0) + percentageOfRatingEqualTo.getOrDefault(unnormalized, (double) 0) / 2 - 0.5);
    }
  }
}
