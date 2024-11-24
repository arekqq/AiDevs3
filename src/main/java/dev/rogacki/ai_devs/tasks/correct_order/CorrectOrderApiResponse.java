package dev.rogacki.ai_devs.tasks.correct_order;

import java.util.List;

public record CorrectOrderApiResponse(
    List<Order> reply,
    String error
) {
}
