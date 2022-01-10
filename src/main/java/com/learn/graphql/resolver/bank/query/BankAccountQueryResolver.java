package com.learn.graphql.resolver.bank.query;

import com.learn.graphql.BankAccountRepository;
import com.learn.graphql.connection.CursorUtil;
import com.learn.graphql.context.CustomGraphQLContext;
import com.learn.graphql.domain.bank.BankAccount;
import com.learn.graphql.domain.bank.Currency;
import graphql.kickstart.tools.GraphQLQueryResolver;
import graphql.relay.Connection;
import graphql.relay.DefaultConnection;
import graphql.relay.DefaultEdge;
import graphql.relay.DefaultPageInfo;
import graphql.relay.Edge;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.SelectedField;
import java.time.Clock;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;

//The way GraphQL works is that it first executes the GraphQLQueryResolver then it executes the normal GraphQLResolver
@Slf4j
@Component
@RequiredArgsConstructor
// you want to query on the root object you should use the GraphQLQueryResolver otherwise if there are being
//queries that are for nested objects we should use GraphQLResolver
public class BankAccountQueryResolver implements GraphQLQueryResolver {

  private final BankAccountRepository bankAccountRepository;
  private final CursorUtil cursorUtil;
  private final Clock clock;

  //The DataFetchingEnvironment contains pretty useful methods to get for example the context(to get the auth), or the selection set
  //which are the fields requested by the user, also to get the arguments, the data loaders
  public BankAccount bankAccount(UUID id, DataFetchingEnvironment environment) {
    log.info("Retrieving bank account id: {}", id);

    //Now, due that we've created a custom context, that is the one which will be used to get access to the
    //Entire context
    CustomGraphQLContext context = environment.getContext();

    log.info("User ID: {}", context.getUserId());

    //The selection set contains the fields that the user wants to see from the query
    //based on the selection set we can apply the contains method which allows us to decide whether we want to apply a special logic
    var requestedFields = environment.getSelectionSet().getFields().stream()
        .map(SelectedField::getName).collect(Collectors.toUnmodifiableSet());

    log.info("Requested Fields: {}", requestedFields);

    return BankAccount.builder()
        .id(id)
        .currency(Currency.USD)
        .createdAt(ZonedDateTime.now(clock))
        .createdOn(LocalDate.now(clock))
        .build();
  }

  //TODO Analyze this code better
  //this is for pagination
  //This is creating the code for the pagination creating edges based on the first and the cursors
  //which will be b64 encoded
  public Connection<BankAccount> bankAccounts(int first, @Nullable String cursor) {
    List<Edge<BankAccount>> edges = getBankAccounts(cursor)
        .stream()
        .map(bankAccount -> new DefaultEdge<>(bankAccount,
            cursorUtil.createCursorWith(bankAccount.getId())))
            //This limits the amount of results based on what's passed through
            .limit(first)
        .collect(Collectors.toUnmodifiableList());

    var pageInfo = new DefaultPageInfo(
        cursorUtil.getFirstCursorFrom(edges),
        cursorUtil.getLastCursorFrom(edges),
        cursor != null,
        edges.size() >= first);

    return new DefaultConnection<>(edges, pageInfo);
  }

  public List<BankAccount> getBankAccounts(String cursor) {
    if (cursor == null) {
      return bankAccountRepository.getBankAccounts();
    }
    return bankAccountRepository.getBankAccountsAfter(cursorUtil.decode(cursor));
  }

}
