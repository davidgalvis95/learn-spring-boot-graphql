package com.learn.graphql.config;

import java.time.Clock;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
//This clock config is pretty useful when dealing with ExtendedScalars that are of type date or datetime
//Because they allow us to deal with the now values and ZonedDateTimes or LocalDateTimes
public class ClockConfig {

  @Bean
  public Clock clock() {
    return Clock.systemUTC();
  }

}
