package com.learn.graphql.context.dataloader;

import com.learn.graphql.service.BalanceService;
import com.learn.graphql.util.CorrelationIdPropagationExecutor;
import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import lombok.RequiredArgsConstructor;
import org.dataloader.DataLoader;
import org.dataloader.DataLoaderRegistry;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataLoaderRegistryFactory {

  private final BalanceService balanceService;

  //This is to pull from the query
  public static final String BALANCE_DATA_LOADER = "BALANCE_DATA_LOADER";
  private static final Executor balanceThreadPool =
      CorrelationIdPropagationExecutor.wrap(
          Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()));

  public DataLoaderRegistry create(String userId) {
    //This registry will be used to this request shared amongst queries
    var registry = new DataLoaderRegistry();
    //Here we will register a data loader, passing a string an the data loader that receives a key and a value
    registry.register(BALANCE_DATA_LOADER, createBalanceDataLoader(userId));
    return registry;
  }

  //This is going to be invoked when the load method is called
  private DataLoader<UUID, BigDecimal> createBalanceDataLoader(String userId) {
    return DataLoader.newMappedDataLoader((Set<UUID> bankAccountIds) ->
        CompletableFuture.supplyAsync(() ->
                balanceService.getBalanceFor(bankAccountIds, userId),
            balanceThreadPool));
  }

}
