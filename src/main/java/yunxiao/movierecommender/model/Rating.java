package yunxiao.movierecommender.model;

public class Rating {
  private int raterId;
  private int movieId;
  private double rating;
  private int timestamp;
  
  public Rating() {
    
  }
  public Rating(int raterId, int movieId, double rating, int timestamp) {
    this.raterId = raterId;
    this.movieId = movieId;
    this.rating = rating;
    this.timestamp = timestamp;
  }
  public int getRaterId() {
    return raterId;
  }
  public void setRaterId(int raterId) {
    this.raterId = raterId;
  }
  public int getMovieId() {
    return movieId;
  }
  public void setMovieId(int movieId) {
    this.movieId = movieId;
  }
  public double getRating() {
    return rating;
  }
  public void setRating(double rating) {
    this.rating = rating;
  }
  public int getTimestamp() {
    return timestamp;
  }
  public void setTimestamp(int timestamp) {
    this.timestamp = timestamp;
  }
  
}
