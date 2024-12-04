package com.multitenant.demo.config;

import com.mongodb.ConnectionString;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.multitenant.demo.dto.TenantContext;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ConcurrentReferenceHashMap;

import java.util.Map;

@Component
@Getter
public class TenantMongoClientProvider {

    private final AppConfig appConfig;
    private final Map<String, MongoClient> mongoClientMap = new ConcurrentReferenceHashMap<>();
    private final Map<String, String> defaultDatabase = new ConcurrentReferenceHashMap<>();

    public TenantMongoClientProvider(@Autowired AppConfig appConfig) {
        this.appConfig = appConfig;
        this.appConfig.getConnStrings().forEach((tenantId, string) -> {
            ConnectionString connectionString = new ConnectionString(string);
            defaultDatabase.put(tenantId, connectionString.getDatabase());
            mongoClientMap.put(tenantId, MongoClients.create(connectionString));
        });
    }

    public MongoClient getTenantMongoClient() {
        String activeTenant = TenantContext.getActiveTenant();
        return mongoClientMap.get(activeTenant);
    }

    public String getDefaultDatabase() {
        return defaultDatabase.get(TenantContext.getActiveTenant());
    }

}
