package dev.rogacki.ai_devs.tasks.calibration;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public final class CalibrationData {
    private String apikey;
    private final String description;
    private final String copyright;
    @JsonProperty("test-data")
    private final List<TestData> testData;
}
