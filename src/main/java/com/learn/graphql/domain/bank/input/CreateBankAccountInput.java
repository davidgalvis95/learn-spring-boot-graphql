package com.learn.graphql.domain.bank.input;

import javax.validation.constraints.NotBlank;
import lombok.Data;

//It's possible to add bean validation in GraphQL using
// javax/hibernate validators
@Data
public class CreateBankAccountInput {

  @NotBlank
  String firstName;
  int age;

}
