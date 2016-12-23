package com.alfy.graphql;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.alfy.graphql.rest.schema.Person;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

public class PersonDataFetcher implements DataFetcher {
  private static final Logger LOGGER = LoggerFactory.getLogger(PersonDataFetcher.class);

  @Override
  public Object get(DataFetchingEnvironment dataFetchingEnvironment) {
    LOGGER.info("Starting " + this.getClass().getSimpleName());
    GraphQLContext context = (GraphQLContext) dataFetchingEnvironment.getContext();
    UserService userService = context.getUserService();

    List<String> ids = dataFetchingEnvironment.getArgument("ids");
    if (CollectionUtils.isEmpty(ids)) {
      String startingPersonId = userService.getStartingPersonId();
      if (Objects.equals(startingPersonId, "NONE")) {
        Map currentUser = userService.getCurrentUser();
        ids = Collections.singletonList((String) currentUser.get("personId"));
      }
      else {
        ids = Collections.singletonList(startingPersonId);
      }
    }

    List<Person> persons = context.getPersonService().getPersonById(ids);
    LOGGER.info("Ending " + this.getClass().getSimpleName());
    return persons;
  }
}
