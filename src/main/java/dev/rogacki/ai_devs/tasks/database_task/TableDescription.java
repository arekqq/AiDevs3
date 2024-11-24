package dev.rogacki.ai_devs.tasks.database_task;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TableDescription(
    @JsonProperty("Table") String table,
    @JsonProperty("Create Table") String createTable
) {
}
