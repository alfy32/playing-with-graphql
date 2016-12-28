package com.alfy.graphql.rest.controller;

import java.util.LinkedHashMap;
import java.util.Map;

import com.alfy.graphql.GraphQLContext;
import com.alfy.graphql.rest.schema.GraphQLRequestBody;
import graphql.ExecutionResult;
import graphql.GraphQL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.WebApplicationContext;

@Controller
@Scope(WebApplicationContext.SCOPE_REQUEST)
public class GraphQLController {
  private static final Logger LOGGER = LoggerFactory.getLogger(GraphQLController.class);

  private final GraphQL graphQL;
  private final GraphQLContext graphQLContext;

  @Autowired
  public GraphQLController(GraphQL graphQL, GraphQLContext graphQLContext) {
    this.graphQL = graphQL;
    this.graphQLContext = graphQLContext;
  }

  @RequestMapping(
      value = "/graphql",
      method = RequestMethod.POST,
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  @ResponseBody
  public Map<String, Object> executeOperation(@RequestBody GraphQLRequestBody requestBody) {
    String query = requestBody.getQuery();
    Map<String, Object> variables = requestBody.getVariables();

    LOGGER.info("Starting graphQL");
    ExecutionResult executionResult = graphQL.execute(query, graphQLContext, variables);
    LOGGER.info("Ending graphQL");

    Map<String, Object> result = new LinkedHashMap<>();
    if (!executionResult.getErrors().isEmpty()) {
      result.put("errors", executionResult.getErrors());
      LOGGER.error("Errors: {}", executionResult.getErrors());
    }
    if (executionResult.getData() != null) {
      result.put("data", executionResult.getData());
    }
    return result;
  }
}
