package yunxiao.movierecommender.controller;

import java.util.Optional;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import yunxiao.movierecommender.model.Person;
import yunxiao.movierecommender.service.PersonService;

public class PersonController {
  
  private final PersonService personService;
  
  @Autowired
  public PersonController(PersonService personService) {
    this.personService = personService;
  }
  
  @GetMapping("/")
  public String index(Model model) {
    model.addAttribute("allPerson", personService.getAllPerson());
    return "index";
  }
  
  @GetMapping("/new")
  public String addPersonForm() {
    return "new";
  }
  
  @PostMapping("/new")
  public String addPerson(HttpServletRequest request, Model model) {
    String name = request.getParameter("name");
    personService.addPerson(new Person(name));
    return "redirect:";
  }
  
  @GetMapping("{id}")
  public String showPerson(@PathVariable UUID id, Model model) {
    Optional<Person> foundPerson = personService.getPersonById(id);
    model.addAttribute("person", foundPerson);
    return "show";
  }
}
