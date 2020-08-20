package yunxiao.movierecommender.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import yunxiao.movierecommender.dao.PersonDao;
import yunxiao.movierecommender.model.Person;

@Service
public class PersonService {
  
  private final PersonDao personDao;
  
  @Autowired
  public PersonService(@Qualifier("postgres-deprecated") PersonDao personDao) {
    this.personDao = personDao;
  }
  
  public int addPerson(Person person) {
    return personDao.insertPerson(person);
  }
  
  public Optional<Person> getPersonById(UUID id) {
    return personDao.getPersonById(id);
  }
  
  public List<Person> getAllPerson() {
    return personDao.getAllPerson();
  }
}
