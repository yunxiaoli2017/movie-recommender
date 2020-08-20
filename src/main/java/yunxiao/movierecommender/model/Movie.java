package yunxiao.movierecommender.model;

public class Movie {
  
  private final int id;
  private int imdbId;
  private String title;
  private String genres;
  private String imdbUrl;
  private String posterUrl;
  
  public Movie() {
    this.id = -1;
  }
  
  public Movie(int id) {
    this.id = id;
  }
  public Movie(int id, int imdbId, String title, String genres, String imdbUrl, String posterUrl) {
    this.id = id;
    this.imdbId = imdbId;
    this.title = title;
    this.genres = genres;
    this.imdbUrl = imdbUrl;
    this.posterUrl = posterUrl;
  }
  
  public boolean isBlank() {
    return (this.id == -1);
  }
  
  public int getId() {
    return id;
  }
  public int getImdbId() {
    return imdbId;
  }
  public void setImdbId(int imdbId) {
    this.imdbId = imdbId;
  }
  public String getTitle() {
    return title;
  }
  public void setTitle(String title) {
    this.title = title;
  }
  public String getPosterUrl() {
    return posterUrl;
  }
  public void setPosterUrl(String posterUrl) {
    this.posterUrl = posterUrl;
  }
  public String getImdbUrl() {
    return imdbUrl;
  }
  public void setImdbUrl(String imdbUrl) {
    this.imdbUrl = imdbUrl;
  }
  public String getGenres() {
    return genres;
  }
  public void setGenres(String genres) {
    this.genres = genres;
  }
  
}
