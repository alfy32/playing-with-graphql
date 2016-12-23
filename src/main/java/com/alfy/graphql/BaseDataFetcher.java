package com.alfy.graphql;

import java.util.HashMap;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

public class BaseDataFetcher implements DataFetcher {
  private static final Logger LOGGER = LoggerFactory.getLogger(BaseDataFetcher.class);

  @Override
  public Object get(DataFetchingEnvironment dataFetchingEnvironment) {
    LOGGER.info("Starting " + this.getClass().getSimpleName());
    HashMap<Object, Object> hashMap = new HashMap<>();
    LOGGER.info("Ending " + this.getClass().getSimpleName());
    return hashMap;
  }
}
