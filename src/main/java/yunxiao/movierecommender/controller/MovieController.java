package yunxiao.movierecommender.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import yunxiao.movierecommender.dto.RatingCreationDto;
import yunxiao.movierecommender.model.Movie;
import yunxiao.movierecommender.model.Rating;
import yunxiao.movierecommender.service.MovieService;
import yunxiao.movierecommender.service.RatingService;

@CrossOrigin(maxAge = 3600)
@Controller
public class MovieController {
  
  private final MovieService movieService;
  private final RatingService ratingService;
  @Value("${use-angular-frontend}")
  private boolean useAngularFrontend;
  
  @Autowired
  public MovieController(MovieService movieService, RatingService ratingService) {
    this.movieService = movieService;
    this.ratingService = ratingService;
  }
  
  @GetMapping("/")
  public String landing() {
    return "landing";
  }
  
  @GetMapping("/rate")
  public List<Movie> getRandomMovies(@RequestParam(defaultValue = "32") int numMovies) {
    List<Movie> randomMovies = movieService.getRandomPopularMovies(numMovies);
    return randomMovies;
  }
  
  @PostMapping("/recommendation")
  public List<Movie> getRecommendedMoviesFromSubmittedRatings(@RequestBody RatingCreationDto form) {
    List<Rating> ratings = form.getRatings();
    ratings = ratingService.filterZeroRating(ratings);
    ratingService.decouplingNormalization(ratings);
    List<Integer> recommendedMovieIds = ratingService.getRecommendedMovieIds(ratings);
    List<Movie> recommendedMovies = movieService.getRecommendedMovies(recommendedMovieIds);
    return recommendedMovies;
  }
  
}
