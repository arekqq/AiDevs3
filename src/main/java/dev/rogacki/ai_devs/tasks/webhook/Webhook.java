package dev.rogacki.ai_devs.tasks.webhook;

import dev.rogacki.ai_devs.ai.Assistant;
import dev.rogacki.ai_devs.external.CentralaClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class Webhook {

    private final Assistant assistant;
    private final CentralaClient centralaClient;

    private static final String[][] DESC_ARR;

    static {
        DESC_ARR = new String[4][4];
        DESC_ARR[0][0] = "start";
        DESC_ARR[0][1] = "trawa";
        DESC_ARR[0][2] = "drzewo";
        DESC_ARR[0][3] = "budynek";

        DESC_ARR[1][0] = "trawa";
        DESC_ARR[1][1] = "wiatrak";
        DESC_ARR[1][2] = "trawa";
        DESC_ARR[1][3] = "trawa";

        DESC_ARR[2][0] = "trawa";
        DESC_ARR[2][1] = "trawa";
        DESC_ARR[2][2] = "kamienie";
        DESC_ARR[2][3] = "dwa drzewa";

        DESC_ARR[3][0] = "skały";
        DESC_ARR[3][1] = "skały";
        DESC_ARR[3][2] = "samochód";
        DESC_ARR[3][3] = "jaskinia";
    }

    @GetMapping("/hello")
    public String healthCheck() {
        return "I'm alive!";
    }

    @PostMapping
    public ResponseEntity<DescriptionResponse> getCurrentField(@RequestBody InstructionRequest request) {
        log.info("===== Instrukcja: {}", request.instruction());
        var response = assistant.getCoord(request.instruction());
        List<Integer> coord = Arrays.stream(response.split(","))
            .map(s -> Integer.parseInt(s.trim()))
            .toList();
        log.info("===== Koordynaty: {}", coord);
        String description = DESC_ARR[coord.get(1)][coord.get(0)];
        log.info("===== Opis: {}", description);
        return ResponseEntity.ok(new DescriptionResponse(description));
    }
}
