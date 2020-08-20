package yunxiao.movierecommender.dto;

import java.util.ArrayList;
import java.util.List;

import yunxiao.movierecommender.model.Rating;

public class RatingCreationDto {
  
  private List<Rating> ratings;
  
  public RatingCreationDto() {
    this.ratings = new ArrayList<Rating>();
  }
  
  public void addRating(Rating rating) {
    this.ratings.add(rating);
  }
  
  public void addEmptyRatings(int num) {
    for (int i = 0; i < num; i++) {
      addRating(new Rating());
    }
  }
  
  public List<Rating> getRatings() {
    return ratings;
  }
}
