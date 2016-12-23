package com.alfy.graphql;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import com.alfy.graphql.rest.SessionId;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

@Component
@Scope(WebApplicationContext.SCOPE_REQUEST)
public class UserService {

  private final RestTemplate restTemplate;
  private final String ftuserUri;
  private final String fsUserUri;
  private final SessionId sessionId;

  private CompletableFuture<Map> currentUserFuture;
  private final String lock = "lock";

  public UserService(RestTemplate restTemplate, Environment environment, SessionId sessionId) {
    this.restTemplate = restTemplate;
    this.ftuserUri = environment.getProperty("ftuser.uri", "http:/familysearch.org/ftuser");
    this.fsUserUri = environment.getProperty("fs-user.uri", "http:/familysearch.org/fs-user");
    this.sessionId = sessionId;
  }

//  public Map getCurrentUser() {
//    return makeCurrentUserRequest();
//  }

  public Map getCurrentUser() {
    synchronized (lock) {
      if (currentUserFuture == null) {
        currentUserFuture = CompletableFuture.supplyAsync(this::makeCurrentUserRequest);
      }
    }
    try {
      return currentUserFuture.get();
    }
    catch (InterruptedException | ExecutionException e) {
      throw new IllegalStateException("Failed to get current user.", e);
    }
  }

  private Map makeCurrentUserRequest() {
    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.set(HttpHeaders.AUTHORIZATION, sessionId.getAuthorizationToken());

    UriComponents uriComponents = UriComponentsBuilder.fromHttpUrl(ftuserUri)
        .path("/users/CURRENT")
        .build();

    ResponseEntity<Map> responseEntity = restTemplate.exchange(uriComponents.toUri(), HttpMethod.GET, new HttpEntity<>(httpHeaders), Map.class);
    return responseEntity.getBody();
  }

  public String getStartingPersonId() {
    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.set(HttpHeaders.AUTHORIZATION, sessionId.getAuthorizationToken());

    UriComponents uriComponents = UriComponentsBuilder.fromHttpUrl(fsUserUri)
        .path("/users/{userId}/preferences/{name}")
        .buildAndExpand(getCurrentUser().get("id"), "tree.startingPersonId");

    ResponseEntity<Map> responseEntity = restTemplate.exchange(uriComponents.toUri(), HttpMethod.GET, new HttpEntity<>(httpHeaders), Map.class);
    Map preference = responseEntity.getBody();
    if (preference == null) {
      return "NONE";
    }
    else if (preference.get("value") == null) {
      return "NONE";
    }
    else {
      return (String) preference.get("value");
    }
  }
}
