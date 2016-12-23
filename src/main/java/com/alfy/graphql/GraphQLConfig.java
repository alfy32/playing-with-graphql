package com.alfy.graphql;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import graphql.GraphQL;
import graphql.Scalars;
import graphql.execution.ExecutionStrategy;
import graphql.execution.ExecutorServiceExecutionStrategy;
import graphql.schema.GraphQLEnumType;
import graphql.schema.GraphQLList;
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

  private static final ExecutorService FIXED_THREAD_POOL = Executors.newFixedThreadPool(50);

  @Bean
  public GraphQL graphQL(GraphQLSchema graphQLSchema, ExecutionStrategy executionStrategy) {
    return new GraphQL(graphQLSchema, executionStrategy);
  }

  @Bean
  public ExecutionStrategy executionStrategy() {
    return new ExecutorServiceExecutionStrategy(FIXED_THREAD_POOL);
  }

  @Bean
  public GraphQLSchema graphQLSchema() {

    GraphQLObjectType queryType = newObject()
        .name("FamilySearchQuery")
        .description("This will define all of the data at FamilySearch")
        .field(newFieldDefinition()
            .type(new GraphQLList(personType()))
            .argument(argument -> argument.name("ids").type(new GraphQLList(Scalars.GraphQLString)))
            .name("persons")
            .dataFetcher(new PersonDataFetcher())
        )
        .field(newFieldDefinition().type(userType()).name("user").dataFetcher(new BaseDataFetcher()))
        .build();

    return GraphQLSchema.newSchema()
        .query(queryType)
        .build();
  }

  private GraphQLObjectType personType() {
    GraphQLEnumType sexEnum = GraphQLEnumType.newEnum().name("Sex").value("MALE").value("FEMALE").build();

    return newObject()
        .name("Person")
        .description("A person from the tree")
        .field(newFieldDefinition().name("status").type(Scalars.GraphQLString))
        .field(newFieldDefinition().name("id").type(Scalars.GraphQLString))
        .field(newFieldDefinition().name("sex").type(sexEnum))
        .field(newFieldDefinition().name("name").type(Scalars.GraphQLString))
        .field(newFieldDefinition().name("lifespan").type(Scalars.GraphQLString))
        .field(newFieldDefinition().name("living").type(Scalars.GraphQLBoolean))
        .field(newFieldDefinition().name("portraitUrl").type(Scalars.GraphQLString).dataFetcher(new PortraitUrlDataFetcher()))
        .field(newFieldDefinition().name("watching").type(Scalars.GraphQLBoolean).dataFetcher(new WatchDataFetcher()))
        .build();
  }

  private GraphQLObjectType userType() {

    GraphQLObjectType profileDataItem = newObject()
        .name("ProfileDataItem")
        .description("Profile Data has a list of key value pairs. I'm not sure how to do that in graphql.")
        .field(newFieldDefinition().name("name").type(Scalars.GraphQLString))
        .field(newFieldDefinition().name("value").type(Scalars.GraphQLString))
        .field(newFieldDefinition().name("shared").type(Scalars.GraphQLBoolean))
        .build();

    UserDataFetcher userDataFetcher = new UserDataFetcher();

    return newObject()
        .name("User")
        .description("A tree user")
        .field(newFieldDefinition().name("birthdate").type(Scalars.GraphQLString).dataFetcher(userDataFetcher))
        .field(newFieldDefinition().name("contactName").type(Scalars.GraphQLString).dataFetcher(userDataFetcher))
        .field(newFieldDefinition().name("country").type(Scalars.GraphQLString).dataFetcher(userDataFetcher))
        .field(newFieldDefinition().name("cpUserId").type(Scalars.GraphQLString).dataFetcher(userDataFetcher))
        .field(newFieldDefinition().name("displayName").type(Scalars.GraphQLBoolean).dataFetcher(userDataFetcher))
        .field(newFieldDefinition().name("email").type(Scalars.GraphQLString).dataFetcher(userDataFetcher))
        .field(newFieldDefinition().name("familyName").type(Scalars.GraphQLString).dataFetcher(userDataFetcher))
        .field(newFieldDefinition().name("gender").type(Scalars.GraphQLString).dataFetcher(userDataFetcher))
        .field(newFieldDefinition().name("givenName").type(Scalars.GraphQLString).dataFetcher(userDataFetcher))
        .field(newFieldDefinition().name("helperAccessPin").type(Scalars.GraphQLString).dataFetcher(userDataFetcher))
        .field(newFieldDefinition().name("id").description("CIS user id").type(Scalars.GraphQLString).dataFetcher(userDataFetcher))
        .field(newFieldDefinition().name("mailingAddress").type(Scalars.GraphQLString).dataFetcher(userDataFetcher))
        .field(newFieldDefinition().name("mailingAddressCity").type(Scalars.GraphQLString).dataFetcher(userDataFetcher))
        .field(newFieldDefinition().name("mailingAddressCountry").type(Scalars.GraphQLString).dataFetcher(userDataFetcher))
        .field(newFieldDefinition().name("mailingAddressPostalCode").type(Scalars.GraphQLString).dataFetcher(userDataFetcher))
        .field(newFieldDefinition().name("mailingAddressProvince").type(Scalars.GraphQLString).dataFetcher(userDataFetcher))
        .field(newFieldDefinition().name("membershipId").type(Scalars.GraphQLString).dataFetcher(userDataFetcher))
        .field(newFieldDefinition().name("personId").type(Scalars.GraphQLString).dataFetcher(userDataFetcher))
        .field(newFieldDefinition().name("phoneNumber").type(Scalars.GraphQLString).dataFetcher(userDataFetcher))
        .field(newFieldDefinition().name("preferredLanguage").type(Scalars.GraphQLString).dataFetcher(userDataFetcher))
        .field(newFieldDefinition().name("profileData").type(new GraphQLList(profileDataItem)).dataFetcher(userDataFetcher))
        .field(newFieldDefinition().name("ward").type(Scalars.GraphQLString).dataFetcher(userDataFetcher))
        .build();
  }
}
