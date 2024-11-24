package dev.rogacki.ai_devs.tasks.correct_order;

import lombok.Builder;

@Builder
public record ApiDbRequest(
    String task,
    String apikey,
    String query
) {

    public ApiDbRequest(String query) {
        this("database", System.getenv("AIDEVS_API_KEY"), query);
    }
}
