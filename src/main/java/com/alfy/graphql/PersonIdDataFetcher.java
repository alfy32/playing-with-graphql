package com.alfy.graphql;

import java.util.Map;
import java.util.Objects;

import com.alfy.graphql.rest.schema.Person;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

public class PersonIdDataFetcher implements DataFetcher {
  private static final Logger LOGGER = LoggerFactory.getLogger(PersonIdDataFetcher.class);

  @Override
  public Object get(DataFetchingEnvironment dataFetchingEnvironment) {
    LOGGER.info("Starting " + this.getClass().getSimpleName());
    GraphQLContext context = (GraphQLContext) dataFetchingEnvironment.getContext();
    UserService userService = context.getUserService();

    Person person;

    String id = dataFetchingEnvironment.getArgument("id");
    if (StringUtils.isEmpty(id)) {
      String startingPersonId = userService.getStartingPersonId();
      if (Objects.equals(startingPersonId, "NONE")) {
        Map currentUser = userService.getCurrentUser();
        String personId = (String) currentUser.get("personId");
        person = new Person(personId);
      }
      else {
        person = new Person(startingPersonId);
      }
    }
    else {
      person = new Person(id);
    }

    LOGGER.info("Ending " + this.getClass().getSimpleName());
    return person;
  }
}
