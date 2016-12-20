package com.alfy.graphql.rest.controller;

import com.alfy.graphql.PersonService;
import com.alfy.graphql.rest.schema.Person;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class PersonController {
  private static final Logger LOGGER = LoggerFactory.getLogger(PersonController.class);

  private final PersonService personService;
  private final String sessionId;

  @Autowired
  public PersonController(PersonService personService, Environment environment) {
    this.personService = personService;
    this.sessionId = environment.getRequiredProperty("sessionId");
  }

  @RequestMapping(
      value = "/person/{id}",
      method = RequestMethod.GET,
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  @ResponseBody
  public Person executeOperation(@PathVariable(name = "id") String personId) {
    return personService.getPersonById(personId, "Bearer " + sessionId);
  }
}
