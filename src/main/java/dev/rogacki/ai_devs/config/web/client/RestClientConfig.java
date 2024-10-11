package dev.rogacki.ai_devs.config.web.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Value("${tasks.baseUrl}")
    public String baseUrl;

    @Bean
    public RestClient tasksClient() {
        return RestClient.builder()
            .baseUrl(baseUrl)
            .defaultStatusHandler(new DefaultResponseErrorHandler())
            .build();
    }
}
