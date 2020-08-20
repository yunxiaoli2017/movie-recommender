package yunxiao.movierecommender.model;

public class Rater {
  
  private int id;
  private double similarity;
  
  public Rater() {
  }
  
  public Rater(int id, double similarity) {
    this.id = id;
    this.similarity = similarity;
  }
  
  public int getId() {
    return id;
  }
  
  public void setId(int id) {
    this.id = id;
  }
  
  public double getSimilarity() {
    return similarity;
  }
  
  public void setSimilarity(double similarity) {
    this.similarity = similarity;
  }
}
