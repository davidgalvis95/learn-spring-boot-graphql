package com.learn.graphql.exceptions;

import graphql.GraphQLError;
import graphql.kickstart.execution.error.GraphQLErrorHandler;
import org.springframework.stereotype.Component;

import java.util.List;

//In order to use this custom error handler we need to set the property
// graphql.servlet.exception-handlers-enabled to false since this is the alternative to the other error handler
@Component
public class CustomGraphQLErrorHandler implements GraphQLErrorHandler {

    @Override
    public List<GraphQLError> processErrors(List<GraphQLError> errors) {
        //in this case we return the same errors
        return errors;
    }
}