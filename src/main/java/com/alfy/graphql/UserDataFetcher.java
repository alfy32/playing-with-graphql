package com.alfy.graphql;

import java.util.Map;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserDataFetcher implements DataFetcher {
  private static final Logger LOGGER = LoggerFactory.getLogger(UserDataFetcher.class);

  @Override
  public Object get(DataFetchingEnvironment dataFetchingEnvironment) {
    LOGGER.info("Starting " + this.getClass().getSimpleName());

    GraphQLContext context = (GraphQLContext) dataFetchingEnvironment.getContext();

    String currentFieldName = dataFetchingEnvironment.getFields().get(0).getName();
    Map currentUser = context.getUserService().getCurrentUser();
    Object value;
    if (currentFieldName.equals("profileData")) {
      value = ((Map) currentUser.get("profileDataList")).get("profileData");
    }
    else {
      value = currentUser.get(currentFieldName);
    }
    LOGGER.info("Got value for " + currentFieldName + "=" + value);

    LOGGER.info("Ending " + this.getClass().getSimpleName());
    return value;
  }
}
