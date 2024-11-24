package dev.rogacki.ai_devs.tasks.database_task;

import com.fasterxml.jackson.annotation.JsonProperty;

public record DataCenters(
    @JsonProperty("dc_id") int dcId
) {
}
