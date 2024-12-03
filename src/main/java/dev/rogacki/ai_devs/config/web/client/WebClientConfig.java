package dev.rogacki.ai_devs.config.web.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${LLAMA_INDEX_URL}")
    public String llamaIndexUrl;

    @Value("${LLAMA_API_KEY}")
    private String apiKey;

    @Bean
    public WebClient llamaWebClient() {
        return WebClient.builder()
            .baseUrl(llamaIndexUrl)
            .defaultHeaders(headers -> {
                headers.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
                headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey);
            })
            .build();
    }
}
