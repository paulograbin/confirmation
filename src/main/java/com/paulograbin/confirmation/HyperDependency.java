package com.paulograbin.confirmation;

import io.hypersistence.optimizer.HypersistenceOptimizer;
import io.hypersistence.optimizer.core.config.JpaConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import javax.persistence.EntityManagerFactory;


@Configuration
public class HyperDependency {

    @Resource
    EntityManagerFactory entityManagerFactory;

    @Bean
    public HypersistenceOptimizer hypersistenceOptimizer() {
        return new HypersistenceOptimizer(new JpaConfig(entityManagerFactory));
    }
}
