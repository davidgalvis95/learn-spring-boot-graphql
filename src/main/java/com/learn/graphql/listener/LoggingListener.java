package com.learn.graphql.listener;

import graphql.kickstart.servlet.core.GraphQLServletListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
//These listeners are used as hooks to perform certain logic based on certain logic
//We do so by implementing the GraphQLServletListener
public class LoggingListener implements GraphQLServletListener {

  //These are called from the AbstractGraphQLHttpServlet
  @Override
  public RequestCallback onRequest(HttpServletRequest request, HttpServletResponse response) {
    return new RequestCallback() {
      @Override
      public void onSuccess(HttpServletRequest request, HttpServletResponse response) {
        // no-op
      }

      @Override
      public void onError(HttpServletRequest request, HttpServletResponse response,
          Throwable throwable) {
        log.error("Caught exception in listener.", throwable);
      }

      @Override
      public void onFinally(HttpServletRequest request, HttpServletResponse response) {
        // This callback will be called post graphql lifecycle.
        // If we are multi-threading we can clear the original NIO thread MDC variables here.
        MDC.clear();
      }
    };
  }

}
