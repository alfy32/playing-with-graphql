package com.alfy.graphql;

import java.util.Map;

import com.alfy.graphql.rest.schema.Person;
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
public class PersonService {

  private final RestTemplate restTemplate;
  private final String tfUri;

  public PersonService(RestTemplate restTemplate, Environment environment) {
    this.restTemplate = restTemplate;
    this.tfUri = environment.getProperty("tf.uri", "http:/familysearch.org/tf");
  }

  public Person getPersonById(String id, String authorizationToken) {
    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.set(HttpHeaders.AUTHORIZATION, authorizationToken);

    UriComponents uriComponents = UriComponentsBuilder.fromHttpUrl(tfUri)
        .path("/person/{id}")
        .buildAndExpand(id);

    ResponseEntity<Map> responseEntity = restTemplate.exchange(uriComponents.toUri(), HttpMethod.GET, new HttpEntity<>(httpHeaders), Map.class);
    Map person = responseEntity.getBody();
    Map summary = (Map) person.get("summary");

    return new Person(
        (String) person.get("id"),
        (String) summary.get("gender"),
        (String) summary.get("name"),
        (String) summary.get("lifespan"),
        (boolean) summary.get("living")
    );
  }
}
