package dev.rogacki.ai_devs.tasks.verify;

import dev.rogacki.ai_devs.ai.Assistant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class VerifyTask implements Runnable {

    @Value("${VERIFY_TASK_URL}")
    public String verifyTaskUrl;
    private final Assistant assistant;

    @Override
    public void run() {
        RestClient client = RestClient.builder().baseUrl(verifyTaskUrl).build();
        var result = client.post()
            .body(Map.of(
                "text", "READY",
                "msgID", "0"
            ))
            .retrieve()
            .body(VerifyResponse.class);
        log.info("Result: {}", result);
        String verifyChatResponse = assistant.verify(result.text());
        log.info(verifyChatResponse);
        VerifyResponse body = client.post()
            .body(new VerifyResponse(result.msgID(), verifyChatResponse))
            .retrieve()
            .body(VerifyResponse.class);
        log.info(body.toString());
    }
}
