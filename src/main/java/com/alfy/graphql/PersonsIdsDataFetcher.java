package com.alfy.graphql;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import com.alfy.graphql.rest.schema.Person;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

public class PersonsIdsDataFetcher implements DataFetcher {
  private static final Logger LOGGER = LoggerFactory.getLogger(PersonsIdsDataFetcher.class);

  @Override
  public Object get(DataFetchingEnvironment dataFetchingEnvironment) {
    LOGGER.info("Starting " + this.getClass().getSimpleName());
    GraphQLContext context = (GraphQLContext) dataFetchingEnvironment.getContext();
    UserService userService = context.getUserService();

    List<Person> persons;

    List<String> ids = dataFetchingEnvironment.getArgument("ids");
    if (CollectionUtils.isEmpty(ids)) {
      String startingPersonId = userService.getStartingPersonId();
      if (Objects.equals(startingPersonId, "NONE")) {
        Map currentUser = userService.getCurrentUser();
        String personId = (String) currentUser.get("personId");
        persons = Collections.singletonList(new Person(personId));
      }
      else {
        persons = Collections.singletonList(new Person(startingPersonId));
      }
    }
    else {
      persons = ids.stream()
          .map(Person::new)
          .collect(Collectors.toList());
    }

    LOGGER.info("Ending " + this.getClass().getSimpleName());
    return persons;
  }
}
