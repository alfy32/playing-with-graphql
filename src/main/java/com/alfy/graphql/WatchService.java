package com.alfy.graphql;

import java.util.Objects;

import com.alfy.graphql.rest.SessionId;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

@Component
@Scope(WebApplicationContext.SCOPE_APPLICATION)
public class WatchService {

  private final RestTemplate restTemplate;
  private final String watchUri;
  private final SessionId sessionId;

  public WatchService(RestTemplate restTemplate, Environment environment, SessionId sessionId) {
    this.restTemplate = restTemplate;
    this.watchUri = environment.getRequiredProperty("watch.uri");
    this.sessionId = sessionId;
  }

  public boolean watchingPerson(String id) {
    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.set(HttpHeaders.AUTHORIZATION, sessionId.getAuthorizationToken());

    UriComponents uriComponents = UriComponentsBuilder.fromHttpUrl(watchUri)
        .path("/watches")
        .queryParam("resourceId", id + "_p_fs-ft_production-primary")
        .buildAndExpand(id);

    ResponseEntity responseEntity = restTemplate.exchange(uriComponents.toUri(), HttpMethod.HEAD, new HttpEntity<>(httpHeaders), String.class);
    return Objects.equals(responseEntity.getStatusCode(), HttpStatus.OK);
  }
}
