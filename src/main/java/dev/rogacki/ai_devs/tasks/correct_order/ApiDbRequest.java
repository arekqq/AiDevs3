package dev.rogacki.ai_devs.tasks.correct_order;

import dev.rogacki.ai_devs.tasks.database_task.ApiDbProperties;
import lombok.Builder;

@Builder
public record ApiDbRequest(
    String task,
    String apikey,
    String query
) {

    public ApiDbRequest(String query) {
        this("database", ApiDbProperties.getApiKey(), query);
    }
}
