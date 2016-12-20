package com.alfy.graphql.rest.controller;

import java.util.Map;

import com.alfy.graphql.rest.schema.GraphQLRequestBody;
import graphql.ExecutionResult;
import graphql.GraphQL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Scope;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.WebApplicationContext;

@Controller
public class GraphQLController {
  private static final Logger LOGGER = LoggerFactory.getLogger(GraphQLController.class);

  private final GraphQL graphQL;

  @Autowired
  public GraphQLController(GraphQL graphQL) {
    this.graphQL = graphQL;
  }

  @RequestMapping(
      value = "/graphql",
      method = RequestMethod.POST,
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  @ResponseBody
  public ExecutionResult executeOperation(@RequestBody GraphQLRequestBody requestBody) {
    String query = requestBody.getQuery();
    Map<String, Object> variables = requestBody.getVariables();

    ExecutionResult executionResult = graphQL.execute(query, (Object) null, variables);

    if (executionResult.getErrors().size() > 0) {
      LOGGER.error("Errors: {}", executionResult.getErrors());
    }

    return executionResult;
  }
}
