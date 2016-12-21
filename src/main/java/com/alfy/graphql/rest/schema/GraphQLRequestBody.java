package com.alfy.graphql.rest.schema;

import java.util.HashMap;
import java.util.Map;

/**
 * The is the expected request body as described at http://graphql.org/learn/serving-over-http/#post-request
 */

public class GraphQLRequestBody {
  private String query;
  private String operationName;
  private Map<String, Object> variables;

  public String getQuery() {
    return query;
  }

  public void setQuery(String query) {
    this.query = query;
  }

  public String getOperationName() {
    return operationName;
  }

  public void setOperationName(String operationName) {
    this.operationName = operationName;
  }

  public Map<String, Object> getVariables() {
    if (variables == null) {
      return new HashMap<>();
    }
    return variables;
  }

  public void setVariables(Map<String, Object> variables) {
    if (variables == null) {
      this.variables = new HashMap<>();
    }
    else {
      this.variables = variables;
    }
  }
}
