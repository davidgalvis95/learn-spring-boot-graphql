package com.learn.graphql.util;

import static com.learn.graphql.instrumentation.RequestLoggingInstrumentation.CORRELATION_ID;

import java.util.concurrent.Executor;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.slf4j.MDC;

@RequiredArgsConstructor(staticName = "wrap")
public class CorrelationIdPropagationExecutor implements Executor {

  private final Executor delegate;

  //This is thread propagation, the delegate is the thread that is wrapped when the wrap method is called over it
  //So here we are executing manually this thread executor, so that we can get the correlation id before executing it
  //later on we'll pass it to the delegate or the wrapped thread so that it can print it as well
  @Override
  public void execute(@NotNull Runnable command) {
    var correlationId = MDC.get(CORRELATION_ID);
    delegate.execute(() -> {
      try {
        MDC.put(CORRELATION_ID, correlationId);
        command.run();
      } finally {
        MDC.remove(CORRELATION_ID);
      }
    });
  }

}
