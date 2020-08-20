package yunxiao.movierecommender.dao;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import yunxiao.movierecommender.model.Person;

@Repository("postgres-deprecated")
public class PersonDataAccessService implements PersonDao {
  
  private final JdbcTemplate jdbcTemplate;
  
  public PersonDataAccessService(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }
  
  @Override
  public int insertPerson(UUID id, Person person) {
    return 0;
  }

  @Override
  public Optional<Person> getPersonById(UUID id) {
    final String sql = "SELECT id, name FROM person WHERE id = ?";
    
    Person person = jdbcTemplate.queryForObject(sql, new Object[] {id}, (resultSet, i) -> {
      UUID personId = UUID.fromString(resultSet.getString("id"));
      String name = resultSet.getString("name");
      return new Person(personId, name);
    });
    return Optional.ofNullable(person);
  }

  @Override
  public List<Person> getAllPerson() {
    final String sql = "SELECT id, name FROM person";
    return jdbcTemplate.query(sql, (resultSet, i) -> {
      UUID id = UUID.fromString(resultSet.getString("id"));
      String name = resultSet.getString("name");
      return new Person(id, name);
    });
  }
}
