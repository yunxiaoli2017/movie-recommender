package yunxiao.movierecommender.dao;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import yunxiao.movierecommender.model.Person;

public interface PersonDao {
  
  int insertPerson(UUID id, Person person);
  
  default int insertPerson(Person person) {
    UUID id = UUID.randomUUID();
    return insertPerson(id, person);
  }
  
  Optional<Person> getPersonById(UUID id);

  List<Person> getAllPerson();
}
