package com.account.account_service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class MicroserviceConfig {
    
    @Value("${transaction.service.url:http://localhost:8082}")
    private String transactionServiceUrl;
    
    @Value("${auth.service.url:http://localhost:8080}")
    private String authServiceUrl;
    
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
    
    public String getTransactionServiceUrl() {
        return transactionServiceUrl;
    }
    
    public String getAuthServiceUrl() {
        return authServiceUrl;
    }
}
