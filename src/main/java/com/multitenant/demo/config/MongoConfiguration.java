package com.multitenant.demo.config;


import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.core.MongoTemplate;

@Configuration
@RequiredArgsConstructor
public class MongoConfiguration {

    private final TenantMongoClientProvider tenantMongoClientProvider;

    @Bean
    @Primary
    public MongoTemplate mongoTemplate() {
        return new MongoTemplate(new TenantAwareDatabaseFactory(tenantMongoClientProvider));
    }

}
