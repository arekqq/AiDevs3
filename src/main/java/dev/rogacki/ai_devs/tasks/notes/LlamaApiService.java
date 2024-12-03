package dev.rogacki.ai_devs.tasks.notes;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.nio.file.Path;
import java.util.Map;

@Slf4j
@Service
public class LlamaApiService {

    private final WebClient webClient;

    public LlamaApiService(WebClient llamaWebClient) {
        this.webClient = llamaWebClient;
    }

    public Mono<String> uploadDocument(Path filePath) {
        log.info("Uploading document: {}", filePath.getFileName());
        return webClient.post()
            .uri("/upload")
            .contentType(MediaType.MULTIPART_FORM_DATA)
            .body(BodyInserters.fromMultipartData("file", new FileSystemResource(filePath)))
            .retrieve()
            .bodyToMono(JsonNode.class)
            .map(response -> response.get("id").asText());
    }

    public Mono<String> checkStatus(String jobId) {
        log.info("Checking status for job: {}", jobId);
        return webClient.get()
            .uri("/job/{jobId}", jobId)
            .retrieve()
            .bodyToMono(JsonNode.class)
            .map(response -> response.get("status").asText());
    }

    public Mono<Map<String, String>> downloadResult(String jobId) {
        log.info("Downloading file: {}", jobId);
        return webClient.get()
            .uri("/job/{jobId}/result/markdown", jobId)
            .retrieve()
            .bodyToMono(new ParameterizedTypeReference<>() {
            });
    }
}
