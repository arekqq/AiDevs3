package dev.rogacki.ai_devs.external;

import dev.rogacki.ai_devs.dto.Answer;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class TaskClient {

    private final RestClient taskRestClient;

    public String getData() {
        return taskRestClient.get()
            .uri("/dane.txt")
            .retrieve()
            .body(String.class);
    }

    public ResponseEntity<Map<String, String>> postAnswer(Answer answer) {
        return taskRestClient.post()
            .uri("/verify")
            .body(answer)
            .retrieve()
            .toEntity(new ParameterizedTypeReference<>() {
            });
    }
}
