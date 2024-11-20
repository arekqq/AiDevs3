package dev.rogacki.ai_devs.external;

import org.slf4j.Logger;
import org.springframework.http.ResponseEntity;

import java.util.Map;

public interface Client {

    ResponseEntity<Map<String, String>> postAnswer(String taskName, Object answerObject);

    default void logResponse(ResponseEntity<Map<String, String>> entity) {
        Logger log = org. slf4j. LoggerFactory. getLogger(Client.class);
        log.info("Answer response status code: {}", entity.getStatusCode());
        log.info("Answer response body: {}", entity.getBody());
        log.info("Answer response headers: {}", entity.getHeaders());
    }
}
