package com.learn.graphql.context;

import graphql.kickstart.servlet.context.GraphQLServletContext;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.security.auth.Subject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.dataloader.DataLoaderRegistry;

//This is a custom context that by implementing the GraphQLServletContext, in this case will be available
//To the DataFetchEnvironment when querying for the context in there, and then to the queries and mutation resolvers
@Getter
@RequiredArgsConstructor
public class CustomGraphQLContext implements GraphQLServletContext {

  private final String userId;
  //We are implementing this one and passing this one as a constructor parameter to make use of all the delegate
  //methods
  private final GraphQLServletContext context;

  @Override
  public List<Part> getFileParts() {
    return context.getFileParts();
  }

  @Override
  public Map<String, List<Part>> getParts() {
    return context.getParts();
  }

  @Override
  public HttpServletRequest getHttpServletRequest() {
    return context.getHttpServletRequest();
  }

  @Override
  public HttpServletResponse getHttpServletResponse() {
    return context.getHttpServletResponse();
  }

  @Override
  public Optional<Subject> getSubject() {
    return context.getSubject();
  }

  @Override
  public Optional<DataLoaderRegistry> getDataLoaderRegistry() {
    return context.getDataLoaderRegistry();
  }

}
