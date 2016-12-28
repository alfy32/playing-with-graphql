package com.alfy.graphql;

import java.util.function.Function;

import com.alfy.graphql.rest.schema.Person;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PersonDataFetcher implements DataFetcher {
  private static final Logger LOGGER = LoggerFactory.getLogger(PersonDataFetcher.class);

  private final Function<Person, Object> function;

  public PersonDataFetcher(Function<Person, Object> function) {
    this.function = function;
  }

  @Override
  public Object get(DataFetchingEnvironment dataFetchingEnvironment) {
    LOGGER.info("Starting " + this.getClass().getSimpleName());
    GraphQLContext context = (GraphQLContext) dataFetchingEnvironment.getContext();


    String personId = ((Person) dataFetchingEnvironment.getSource()).getId();
    Person person = context.getPersonService().getPersonById(personId).join();

    Object value = function.apply(person);

    LOGGER.info("Ending " + this.getClass().getSimpleName());
    return value;
  }
}
