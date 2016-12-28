package com.alfy.graphql.rest.schema;


public class Person {
  private String status;
  private String id;
  private String sex;
  private String name;
  private String lifespan;
  private boolean living;

  public Person(String id) {
    this.id = id;
  }

  public Person(String status, String id, String sex, String name, String lifespan, boolean living) {
    this.status = status;
    this.id = id;
    this.sex = sex;
    this.name = name;
    this.lifespan = lifespan;
    this.living = living;
  }

  public String getStatus() {
    return status;
  }

  public String getId() {
    return id;
  }

  public String getSex() {
    return sex;
  }

  public String getName() {
    return name;
  }

  public String getLifespan() {
    return lifespan;
  }

  public boolean isLiving() {
    return living;
  }
}
