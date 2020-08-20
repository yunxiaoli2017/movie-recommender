package yunxiao.movierecommender.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import yunxiao.movierecommender.model.Person;

@Repository("fakeDao")
public class FakePersonDataAccessService implements PersonDao {
  
  private static List<Person> DB = new ArrayList<>();
  
  @Override
  public int insertPerson(UUID id, Person person) {
    DB.add(new Person(id, person.getName()));
    return 1;
  }

  @Override
  public Optional<Person> getPersonById(UUID id) {
    Person foundPerson = new Person("Not Found");
    for (int i = 0; i < DB.size(); i++) {
      if (DB.get(i).getId().equals(id)) {
        foundPerson = DB.get(i);
      }
    }
    return Optional.ofNullable(foundPerson);
  }

  @Override
  public List<Person> getAllPerson() {
    return FakePersonDataAccessService.DB;
  }
}
