package dev.rogacki.ai_devs.external;

import dev.rogacki.ai_devs.dto.Answer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Component
@Slf4j
public class CentralaClient implements Client {

    private final RestClient taskRestClient;

    public CentralaClient(@Qualifier("centralaReportClient") RestClient taskRestClient) {
        this.taskRestClient = taskRestClient;
    }

    @Override
    public ResponseEntity<Map<String, String>> postAnswer(Answer answer) {
        ResponseEntity<Map<String, String>> entity = taskRestClient.post()
            .uri("/verify")
            .body(answer)
            .retrieve()
            .toEntity(new ParameterizedTypeReference<>() {
            });
        logResponse(entity);
        return entity;
    }
}
