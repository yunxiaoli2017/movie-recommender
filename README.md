# Movie Recommender

A Spring Boot web app that generates movie recommendations from a user's submitted ratings running a user-based collaborative filtering recommendation engine.


To see the app in action, go to  [https://movie-recommender-demo.herokuapp.com/](https://movie-recommender-demo.herokuapp.com/)

### Dataset
Original and processed movieLens dataset, as well as processing steps can be found at another repository [27M-movieLens-dataset-processing](https://github.com/yunxiaoli2017/27M-movieLens-dataset-processing). Main processings are:
* Scrap poster urls from IMDB
* Normalize ratings with decoupling normalization
* Extract popular movies dataset and compact ratings dataset.

### Framework & Database

* [Spring Boot](https://spring.io/)
* [PostgresQL](https://www.postgresql.org/) (served on [AWS](https://aws.amazon.com/rds/))
* [Flyway](https://flywaydb.org/)

### Algorithm

* User-based collaborative filtering
  * Draw a number of movies for the user to rate. 
  (Only draw from movies with > 5,000 ratings to increase the chance that the user has known some of them before)
 
  * Based on submitted ratings, find raters in database who have also rated the same movies and compute similarities with the user for all raters found. 
  (To reduce time cost, compact ratings dataset can be used at this step)
 
  * Search for ratings of all raters found, and compute predicted scores on movies they rated. 
  (To reduce time cost, 100 most similar raters are used)

  * Sort out movies with highest scores and recommend to the user. 
  (Aside from top 10 from popular movies with > 5,000 ratings, top 10 from unpopular ones with < 5,000 ratings are also recommended to tackle 'long tail' problem, as to provide harder-to-find, unexpected discoveries)
  
### Other Tools

* [Heroku](https://www.heroku.com/)
* [Thymeleaf](https://www.thymeleaf.org/)
* [Bootstrap](https://getbootstrap.com/)

## License

#### [MIT](./LICENSE)
