package dev.rogacki.ai_devs.tasks.database_task;

import java.util.List;

public record ApiDbResponse<T>(
    List<T> reply,
    String error
) {
}
