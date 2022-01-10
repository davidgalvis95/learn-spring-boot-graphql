package com.learn.graphql.instrumentation;

import graphql.ExecutionResult;
import graphql.execution.instrumentation.InstrumentationContext;
import graphql.execution.instrumentation.SimpleInstrumentation;
import graphql.execution.instrumentation.SimpleInstrumentationContext;
import graphql.execution.instrumentation.parameters.InstrumentationExecutionParameters;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RequestLoggingInstrumentation extends SimpleInstrumentation {

  //Tasks to add the correlation id and propagate the logs downstream to each node
  //1. Add correlation id to MDC
  //2. Print correlation id in the logs
  //3. Pass correlation id to async resolver threads
  //4. Pass correlation id to dataloader threads
  //5. Ensure correlation id is cleared

  public static final String CORRELATION_ID = "correlation_id";

  private final Clock clock;

  //This instrumentation allows us to log the activity that is executed by the query alone, knowing what has failed in the query and what has not
  @Override
  public InstrumentationContext<ExecutionResult> beginExecution(
      InstrumentationExecutionParameters parameters) {
    var start = Instant.now(clock);
    // Add the correlation ID to the NIO thread
    //The correlation id is to trace all the stuff down to the embedded resolvers which are not visible to us
    //The MDC will store the logging variables in the thread local
    //We take the execution id and map it to the correlation one because we want it to be propagated downstream
    MDC.put(CORRELATION_ID, parameters.getExecutionInput().getExecutionId().toString());

    log.info("Query: {} with variables: {}", parameters.getQuery(), parameters.getVariables());
    return SimpleInstrumentationContext.whenCompleted((executionResult, throwable) -> {
      var duration = Duration.between(start, Instant.now(clock));
      if (throwable == null) {
        log.info("Completed successfully in: {}", duration);
      } else {
        log.warn("Failed in: {}", duration, throwable);
      }
      // If we have async resolvers, this callback can occur in the thread-pool and not the NIO thread.
      // In that case, the LoggingListener will be used as a fallback to clear the NIO thread.
      MDC.clear();
    });
  }

}
