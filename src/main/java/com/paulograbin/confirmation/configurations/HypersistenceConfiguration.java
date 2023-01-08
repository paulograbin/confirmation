package com.paulograbin.confirmation.configurations;

import io.hypersistence.optimizer.HypersistenceOptimizer;
import io.hypersistence.optimizer.core.config.JpaConfig;
import io.hypersistence.optimizer.hibernate.event.configuration.schema.SchemaGenerationEvent;
import io.hypersistence.optimizer.hibernate.event.mapping.identifier.IdentityGeneratorEvent;
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
        return new HypersistenceOptimizer(new JpaConfig(entityManagerFactory)
                .setEventFilter(event -> !(event instanceof IdentityGeneratorEvent))
                .setEventFilter(event -> !(event instanceof SchemaGenerationEvent)));
    }
}