package dev.rogacki.ai_devs.tasks.interrogation;


import dev.rogacki.ai_devs.ai.Assistant;
import dev.rogacki.ai_devs.dto.Answer;
import dev.rogacki.ai_devs.external.CentralaClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class InterrogationTask implements Runnable {

    @Value("${INTERROGATIONS_PATH}")
    public String interrogationsPath;
    @Value("${AIDEVS_API_KEY}")
    private String apiKey;

    private final Assistant assistant;
    private final CentralaClient centralaClient;

    private static final String QUESTION = """
        Na jakiej ulicy znajduje się uczelnia, na której wykłada Andrzej Maj?
        """;

    @Override
    public void run() {
        List<String> strings = readTxtFiles();
        log.info(strings.toString());
        String response = assistant.interrogation(strings, QUESTION);
        centralaClient.postAnswer(new Answer("mp3", apiKey, response));
    }

    public List<String> readTxtFiles() {
        List<String> allLines = new ArrayList<>();
        try (Stream<Path> filePathStream = Files.list(Paths.get(interrogationsPath))) {
            List<Path> txtFiles = filePathStream
                .filter(path -> path.toString().endsWith(".txt"))
                .toList();
            for (Path filePath : txtFiles) {
                List<String> lines = Files.readAllLines(filePath);
                allLines.addAll(lines);
            }
        } catch (IOException e) {
            log.error("Error reading files", e);
        }
        return allLines;
    }
}
