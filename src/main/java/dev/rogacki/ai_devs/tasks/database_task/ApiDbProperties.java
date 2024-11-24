package dev.rogacki.ai_devs.tasks.database_task;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ApiDbProperties {

    @Getter
    private static String apiKey;

    @Value("${AIDEVS_API_KEY}")
    public void setApiKey(String value) {
        apiKey = value;
    }

}
