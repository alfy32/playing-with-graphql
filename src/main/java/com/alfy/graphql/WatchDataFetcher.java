package com.alfy.graphql;

import com.alfy.graphql.rest.schema.Person;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WatchDataFetcher implements DataFetcher {
  private static final Logger LOGGER = LoggerFactory.getLogger(WatchDataFetcher.class);

  @Override
  public Object get(DataFetchingEnvironment dataFetchingEnvironment) {
    LOGGER.info("Starting " + this.getClass().getSimpleName());

    Object value = null;

    if (dataFetchingEnvironment.getSource() instanceof Person) {
      GraphQLContext context = (GraphQLContext) dataFetchingEnvironment.getContext();

      Person person = (Person) dataFetchingEnvironment.getSource();
      String personId = person.getId();

      value = context.getWatchService().watchingPerson(personId);
    }

    LOGGER.info("Ending " + this.getClass().getSimpleName());
    return value;
  }
}
