package dev.rogacki.ai_devs.tasks.correct_order;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Order(
    @JsonProperty("base_id") int baseId,
    String letter,
    int weight
) {
}
