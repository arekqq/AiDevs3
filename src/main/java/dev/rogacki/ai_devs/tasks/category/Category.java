package dev.rogacki.ai_devs.tasks.category;

import com.fasterxml.jackson.annotation.JsonValue;

public enum Category {
    PERSON,
    HARDWARE,
    NONE;

    @JsonValue
    @Override
    public String toString() {
        return name().toLowerCase();
    }

}
