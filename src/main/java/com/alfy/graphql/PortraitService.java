package com.alfy.graphql;

import java.util.Map;
import java.util.Optional;

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
@Scope(WebApplicationContext.SCOPE_APPLICATION)
public class PortraitService {

  private final RestTemplate restTemplate;
  private final String artifactManagerUri;

  public PortraitService(RestTemplate restTemplate, Environment environment) {
    this.restTemplate = restTemplate;
    this.artifactManagerUri = environment.getRequiredProperty("artifact-manager.uri");
  }

  public String getPersonById(String id, String authorizationToken) {
    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.set(HttpHeaders.AUTHORIZATION, authorizationToken);

    UriComponents uriComponents = UriComponentsBuilder.fromHttpUrl(artifactManagerUri)
        .path("/persons/personsByTreePersonId/{0}/summary")
        .buildAndExpand(id);

    ResponseEntity<Map> responseEntity = restTemplate.exchange(uriComponents.toUri(), HttpMethod.GET, new HttpEntity<>(httpHeaders), Map.class);
    Map treePersonSummary = responseEntity.getBody();

    return Optional
        .ofNullable((String) treePersonSummary.get("thumbSquareUrl"))
        .orElse("broken");
  }
}
