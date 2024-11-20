package dev.rogacki.ai_devs.external;

import dev.rogacki.ai_devs.dto.Answer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Component
@Slf4j
public class TaskClient implements Client {

    private final RestClient taskRestClient;

    @Value("${AIDEVS_API_KEY}")
    public String apiKey;

    public TaskClient(@Qualifier("tasksClient") RestClient taskRestClient) { // TODO try to lombokize it
        this.taskRestClient = taskRestClient;
    }

    public String getData() {
        return taskRestClient.get()
            .uri("/dane.txt")
            .retrieve()
            .body(String.class);
    }

    @Override
    public ResponseEntity<Map<String, String>> postAnswer(String taskName, Object answerObject) {
        var answer = new Answer(taskName, apiKey, answerObject);
        ResponseEntity<Map<String, String>> entity = taskRestClient.post()
            .uri("/verify")
            .body(answer)
            .retrieve()
            .toEntity(new ParameterizedTypeReference<>() {
            });
        log.info("Answer response status code: {}", entity.getStatusCode());
        log.info("Answer response body: {}", entity.getBody());
        log.info("Answer response headers: {}", entity.getHeaders());
        return entity;
    }
}
