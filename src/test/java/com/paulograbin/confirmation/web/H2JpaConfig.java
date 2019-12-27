package com.paulograbin.confirmation.web;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;


@Configuration
@EnableJpaRepositories(basePackages = "com.paulograbin.confirmation")
@EnableTransactionManagement
public class H2JpaConfig {
}
