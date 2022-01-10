package com.learn.graphql.resolver.bank.query;

import com.learn.graphql.context.dataloader.DataLoaderRegistryFactory;
import com.learn.graphql.domain.bank.Asset;
import com.learn.graphql.domain.bank.BankAccount;
import com.learn.graphql.domain.bank.Client;
import com.learn.graphql.util.CorrelationIdPropagationExecutor;
import graphql.execution.DataFetcherResult;
import graphql.kickstart.execution.error.GenericGraphQLError;
import graphql.kickstart.tools.GraphQLResolver;
import graphql.schema.DataFetchingEnvironment;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import lombok.extern.slf4j.Slf4j;
import org.dataloader.DataLoader;
import org.springframework.stereotype.Component;

@Slf4j
@Component
//This is the resolver that will deal with the nested objects
public class BankAccountResolver implements GraphQLResolver<BankAccount> {

    private static final Executor executor =
            CorrelationIdPropagationExecutor.wrap(
                    Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()));

    //This is an async resolver that improves the way we can get the fields from the query when they do
    //not depend on each other, because by default they are executed in order and synchronous way, for example if
    //I have assets and client fields, and they do not depend on each other, I can use this kind of async resolvers
    public CompletableFuture<List<Asset>> assets(BankAccount bankAccount) {
        return CompletableFuture.supplyAsync(
                () -> {
                    log.info("Getting assets for bank account id {}", bankAccount.getId());
                    return List.of();
                },
                executor);
    }

    public Client client(BankAccount bankAccount) {
        log.info("Requesting client data for bank account id {}", bankAccount.getId());
        return Client.builder()
                .id(UUID.randomUUID())
                .firstName("Philip")
                .lastName("Starritt")
                .build();
    }

    //This data fetcher is used to get data when some data is partial, or some fields could not be gotten
    public DataFetcherResult<Client> getDataFetcherClient(BankAccount bankAccount) {
        log.info("Requesting client data for bank account id {}", bankAccount.getId());
        return DataFetcherResult.<Client>newResult()
                .data(Client.builder()
                        .id(UUID.randomUUID())
                        .firstName("Philip")
                        .lastName("Starritt")
                        .build())
                .error(new GenericGraphQLError("Could not get the whole data"))
                .build();
    }
    //What is going to happen here is that the data loader will load the bak account ids while it's being executed
    //asynchronously then the load call will execute the dataloader by creating it, thus calling the newMappedDataLoader
    //in the created DataLoaderRegistryFactory which will fetch the balance for specific bank account ids
    //in this case is hardcoded, but we can provide them
    public CompletableFuture<BigDecimal> balance(BankAccount bankAccount,
                                                 DataFetchingEnvironment environment) {
        DataLoader<UUID, BigDecimal> dataLoader = environment
                .getDataLoader(DataLoaderRegistryFactory.BALANCE_DATA_LOADER);
        return dataLoader.load(bankAccount.getId());
    }

}
