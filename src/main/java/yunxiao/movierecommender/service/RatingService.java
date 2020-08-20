package yunxiao.movierecommender.service;

import java.util.ArrayList;
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

import yunxiao.movierecommender.dao.RatingDao;
import yunxiao.movierecommender.model.Rater;
import yunxiao.movierecommender.model.Rating;

@Service
public class RatingService {
  
  private final RatingDao ratingDao;
  static public int MAX_RECOMMENDATIONS = 20;
  static public int MAX_RECOMMENDING_RATERS = 100;
  
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
    List<Integer> topMovies = getTopMovieIds(movieScores);
    return topMovies;
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

  private List<Integer> getTopMovieIds(Map<Integer, Double> scores) {
    int numMovie = Integer.min(MAX_RECOMMENDATIONS, scores.size());
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
  
  public List<Rating> filterZeroAndThreeRating(List<Rating> ratings) {
    List<Rating> filteredRatings = new ArrayList<>();
    for (Rating r : ratings) {
      if (r.getRating() > 0.1 && (r.getRating() < 2.9 || r.getRating() > 3.1)) {
        filteredRatings.add(r);
      }
    }
    return filteredRatings;
  }
}
