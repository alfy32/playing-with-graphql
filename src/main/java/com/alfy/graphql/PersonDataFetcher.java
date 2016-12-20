package com.alfy.graphql;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class PersonDataFetcher implements DataFetcher {

  private final PersonService personService;
  private final String sessionId;

  @Autowired
  public PersonDataFetcher(PersonService personService, Environment environment) {
    this.personService = personService;
    this.sessionId = environment.getRequiredProperty("sessionId");
  }

  @Override
  public Object get(DataFetchingEnvironment dataFetchingEnvironment) {
    String id = dataFetchingEnvironment.getArgument("id");
    if (id == null) {
      throw new IllegalArgumentException("id is required.");
    }

    return personService.getPersonById(id, "Bearer " + sessionId);
  }

}
