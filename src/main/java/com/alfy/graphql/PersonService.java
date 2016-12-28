package com.alfy.graphql;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import com.alfy.graphql.rest.SessionId;
import com.alfy.graphql.rest.schema.Person;
import org.springframework.context.annotation.Scope;
import org.springframework.core.ParameterizedTypeReference;
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
public class PersonService {

  private final RestTemplate restTemplate;
  private final String tfUri;
  private final SessionId sessionId;

  private final Map<String, CompletableFuture<Person>> inFlightPersons;

  public PersonService(RestTemplate restTemplate, Environment environment, SessionId sessionId) {
    this.restTemplate = restTemplate;
    this.tfUri = environment.getProperty("tf.uri", "http:/familysearch.org/tf");
    this.sessionId = sessionId;
    this.inFlightPersons = new ConcurrentHashMap<>();
  }

  public synchronized CompletableFuture<Person> getPersonById(String id) {
    if (inFlightPersons.containsKey(id)) {
      return inFlightPersons.get(id);
    }

    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.set(HttpHeaders.AUTHORIZATION, sessionId.getAuthorizationToken());

    UriComponents uriComponents = UriComponentsBuilder.fromHttpUrl(tfUri)
        .path("/person/{id}/summary")
        .buildAndExpand(id);

    CompletableFuture<Person> personCompletableFuture = CompletableFuture.supplyAsync(() -> {
      ResponseEntity<Map> responseEntity = restTemplate.exchange(uriComponents.toUri(), HttpMethod.GET, new HttpEntity<>(httpHeaders), Map.class);
      Map person = responseEntity.getBody();
      Map summary = (Map) person.get("summary");

      return new Person(
          "OK",
          (String) person.get("id"),
          (String) summary.get("gender"),
          (String) summary.get("name"),
          (String) summary.get("lifespan"),
          (boolean) summary.get("living")
      );
    });

    inFlightPersons.put(id, personCompletableFuture);
    return personCompletableFuture;
  }

  public List<Person> getPersonsByIds(List<String> ids) {
    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.set(HttpHeaders.AUTHORIZATION, sessionId.getAuthorizationToken());

    UriComponents uriComponents = UriComponentsBuilder.fromHttpUrl(tfUri)
        .path("/person/summary")
        .queryParam("id", ids.toArray())
        .build();

    ResponseEntity<Map<String, Map>> responseEntity = restTemplate.exchange(uriComponents.toUri(), HttpMethod.GET, new HttpEntity<>(httpHeaders), new ParameterizedTypeReference<Map<String, Map>>() {});
    Map<String, Map> response = responseEntity.getBody();

    return ids.stream()
        .map(response::get)
        .map(person -> {
          String status = (String) person.get("status");
          Map personCardSummary = (Map) person.get("personCardSummary");
          Map summary = (Map) personCardSummary.get("summary");

          return new Person(
              status,
              (String) personCardSummary.get("id"),
              (String) summary.get("gender"),
              (String) summary.get("name"),
              (String) summary.get("lifespan"),
              (boolean) summary.get("living")
          );
        })
        .collect(Collectors.toList());
  }
}
