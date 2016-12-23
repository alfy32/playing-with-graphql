package com.alfy.graphql;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

@Component
@Scope(WebApplicationContext.SCOPE_REQUEST)
public class GraphQLContext {

  private final PersonService personService;
  private final UserService userService;
  private final PortraitService portraitService;
  private final WatchService watchService;

  @Autowired
  public GraphQLContext(PersonService personService, UserService userService, PortraitService portraitService, WatchService watchService) {
    this.personService = personService;
    this.userService = userService;
    this.portraitService = portraitService;
    this.watchService = watchService;
  }

  public PersonService getPersonService() {
    return personService;
  }

  public UserService getUserService() {
    return userService;
  }

  public PortraitService getPortraitService() {
    return portraitService;
  }

  public WatchService getWatchService() {
    return watchService;
  }
}
