package com.alfy.graphql;

import com.alfy.graphql.rest.schema.Person;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class PortraitUrlDataFetcher implements DataFetcher {

  private final PortraitService portraitService;
  private final String sessionId;

  public PortraitUrlDataFetcher(PortraitService portraitService, Environment environment) {
    this.portraitService = portraitService;
    this.sessionId = environment.getRequiredProperty("sessionId");
  }

  @Override
  public Object get(DataFetchingEnvironment dataFetchingEnvironment) {
    if (dataFetchingEnvironment.getSource() instanceof Person) {
      Person person = (Person) dataFetchingEnvironment.getSource();
      String personId = person.getId();

      return portraitService.getPersonById(personId, sessionId);
    }

    return null;
  }
}
