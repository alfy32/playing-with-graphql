package com.alfy.graphql;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.alfy.graphql.rest.schema.Person;
import graphql.GraphQL;
import graphql.Scalars;
import graphql.schema.GraphQLEnumType;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLSchema;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.web.context.WebApplicationContext;

import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static graphql.schema.GraphQLObjectType.newObject;

@Configuration
@Scope(WebApplicationContext.SCOPE_APPLICATION)
public class GraphQLConfig {

  private final List<Person> persons;

  public GraphQLConfig() {
    this.persons = new ArrayList<>();
    persons.add(new Person("MMMM-MMM", "MALE", "Male Person", "1825-1910", false));
    persons.add(new Person("MMMM-FFF", "FEMALE", "Female Person", "1826-1920", true));
  }

  @Bean
  public GraphQL graphQL(GraphQLSchema graphQLSchema) {
    return new GraphQL(graphQLSchema);
  }

  @Bean
  public GraphQLSchema graphQLSchema(PersonDataFetcher personDataFetcher, PortraitUrlDataFetcher portraitUrlDataFetcher) {
    GraphQLObjectType queryType = newObject()
        .name("PersonQuery")
        .description("Persons Data")
        .field(newFieldDefinition()
            .type(personType(portraitUrlDataFetcher))
            .argument(argument -> argument.name("id").type(Scalars.GraphQLString))
            .name("person")
            .dataFetcher(personDataFetcher)
        )
        .build();

    return GraphQLSchema.newSchema()
        .query(queryType)
        .build();
  }

  private GraphQLObjectType personType(PortraitUrlDataFetcher portraitUrlDataFetcher) {
    GraphQLEnumType sexEnum = GraphQLEnumType.newEnum().name("Sex").value("MALE").value("FEMALE").build();

    return newObject()
        .name("Person")
        .description("A person from the tree")
        .field(newFieldDefinition().name("id").type(Scalars.GraphQLString))
        .field(newFieldDefinition().name("sex").type(sexEnum))
        .field(newFieldDefinition().name("name").type(Scalars.GraphQLString))
        .field(newFieldDefinition().name("lifespan").type(Scalars.GraphQLString))
        .field(newFieldDefinition().name("living").type(Scalars.GraphQLBoolean))
        .field(newFieldDefinition().name("portraitUrl").type(Scalars.GraphQLString).dataFetcher(portraitUrlDataFetcher))
        .build();
  }
}
