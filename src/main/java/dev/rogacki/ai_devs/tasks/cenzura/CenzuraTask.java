package dev.rogacki.ai_devs.tasks.cenzura;

import dev.rogacki.ai_devs.ai.Assistant;
import dev.rogacki.ai_devs.external.CentralaClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

    @Slf4j
    @Service
    @RequiredArgsConstructor
    public class CenzuraTask implements Runnable {

        @Value("${CENZURA_TASK_URL}")
        public String cenzuraTaskUrl;

        private final CentralaClient centralaClient;
        private final Assistant assistant;

    @Override
    public void run() {

        RestClient client = RestClient.builder().baseUrl(cenzuraTaskUrl).build();
        ResponseEntity<String> response = client.get().retrieve().toEntity(String.class);
        log.info(response.getBody());
        String censor = assistant.censor(response.getBody());
        log.info(censor);

        centralaClient.postAnswer("CENZURA", censor);
    }
}
