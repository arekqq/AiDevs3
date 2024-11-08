package dev.rogacki.ai_devs.tasks.calibration;

import dev.rogacki.ai_devs.ai.Assistant;
import dev.rogacki.ai_devs.dto.Answer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CalibrationTask implements Runnable {

    @Value("${CALIBRATION_TASK_URL}")
    public String calibrationTaskUrl;

    @Value("${CENTRALA_URL}")
    public String centralaUrl;

    private final Assistant assistant;

    @Override
    public void run() {

        RestClient client = RestClient.builder().baseUrl(calibrationTaskUrl).build();
        CalibrationData body = client.get().retrieve().body(CalibrationData.class);
        String aidevsApiKey = System.getenv("AIDEVS_API_KEY");
        body.setApikey(aidevsApiKey);
        body.getTestData().forEach(this::checkTestData);
        log.info(body.toString());
        ResponseEntity<Map<String, String>> result = RestClient.builder().baseUrl(centralaUrl).build()
            .post()
            .body(new Answer("JSON", aidevsApiKey, body))
            .retrieve()
            .toEntity(new ParameterizedTypeReference<>() {
            });
        log.info(result.getStatusCode().toString());
        log.info(result.getBody().toString());

    }

    private void checkTestData(TestData testData) {
        Optional.ofNullable(testData.test)
            .ifPresent(td -> td.setA(assistant.answerBriefly(td.q)));
        testData.setAnswer(calculateExpression(testData.getQuestion()));
    }

    private int calculateExpression(String expression) {
        String[] parts = expression.split("\\+");
        int num1 = Integer.parseInt(parts[0].trim());
        int num2 = Integer.parseInt(parts[1].trim());
        return num1 + num2;
    }
}
