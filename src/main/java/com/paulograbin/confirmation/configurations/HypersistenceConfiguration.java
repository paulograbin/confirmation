package com.paulograbin.confirmation.configurations;

import io.hypersistence.optimizer.HypersistenceOptimizer;
import io.hypersistence.optimizer.core.config.JpaConfig;
import jakarta.annotation.Resource;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class HypersistenceConfiguration {

    @Resource
    EntityManagerFactory entityManagerFactory;

    @Bean
    public HypersistenceOptimizer hypersistenceOptimizer() {
        return new HypersistenceOptimizer(new JpaConfig(entityManagerFactory));
    }
}