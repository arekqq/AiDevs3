package dev.rogacki.ai_devs.external;

import dev.rogacki.ai_devs.dto.Answer;
import dev.rogacki.ai_devs.tasks.correct_order.ApiDbRequest;
import dev.rogacki.ai_devs.tasks.correct_order.CorrectOrderApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Map;
import java.util.Set;

@Component
@Slf4j
public class CentralaClient implements Client {

    private final RestClient taskRestClient;

    @Value("${AIDEVS_API_KEY}")
    public String apiKey;

    public CentralaClient(@Qualifier("centralaReportClient") RestClient taskRestClient) {
        this.taskRestClient = taskRestClient;
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
        logResponse(entity);
        return entity;
    }

    public CorrectOrderApiResponse getCorrectOrder(ApiDbRequest request) {
        return taskRestClient.post()
            .uri("/apidb")
            .body(request)
            .retrieve()
            .body(CorrectOrderApiResponse.class);
    }

    public <T> T queryApiDb(String query, ParameterizedTypeReference<T> type) {
        var request = new ApiDbRequest(query);
        return taskRestClient.post()
            .uri("/apidb")
            .body(request)
            .retrieve()
            .body(type);
    }

    public Set<String> peopleSearch(String name) {
        var body = taskRestClient.post()
            .uri("people")
            .body(Map.of(
                "apikey", apiKey,
                "query", name))
            .retrieve()
            .body(SearchResponse.class);
        return Set.of(body.message.split(" "));
    }

    public String retrieveNote() {
        return taskRestClient.get()
            .uri("/dane/barbara.txt")
            .retrieve()
            .body(String.class);
    }

    public Set<String> citySearch(String city) {
        var body = taskRestClient.post()
            .uri("places")
            .body(Map.of(
                "apikey", apiKey,
                "query", city))
            .retrieve()
            .body(SearchResponse.class);
        return Set.of(body.message.split(" "));
    }

    record SearchResponse(
        String code,
        String message
    ) {}
}
