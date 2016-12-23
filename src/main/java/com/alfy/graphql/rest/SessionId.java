package com.alfy.graphql.rest;


import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

@Component
@Scope(WebApplicationContext.SCOPE_REQUEST)
public class SessionId {

  private final String sessionId;

  public SessionId(Environment environment) {
    this.sessionId = environment.getRequiredProperty("sessionId");
  }

  public String getSessionId() {
    return sessionId;
  }

  public String getAuthorizationToken() {
    return "Bearer " + getSessionId();
  }
}
