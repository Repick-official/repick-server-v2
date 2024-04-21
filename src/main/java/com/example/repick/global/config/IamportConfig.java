package com.example.repick.global.config;

import com.siot.IamportRestClient.IamportClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class IamportConfig {

    @Value("${iamport.apiKey}")
    private String apiKey;

    @Value("${iamport.apiSecret}")
    private String apiSecret;

    @Bean
    public IamportClient iamportClient() {
        return new IamportClient(apiKey, apiSecret);
    }

}
