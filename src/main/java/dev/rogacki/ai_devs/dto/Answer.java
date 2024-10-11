package dev.rogacki.ai_devs.dto;

public record Answer(
    String task,
    String apikey,
    Object answer
) {
}
