package dev.rogacki.ai_devs.tasks.category;

import dev.rogacki.ai_devs.ai.Assistant;
import dev.rogacki.ai_devs.dto.Answer;
import dev.rogacki.ai_devs.external.CentralaClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryTask implements Runnable {

    private final Assistant assistant;
    private final CentralaClient centralaClient;

    @Value("${AIDEVS_API_KEY}")
    public String aidevsApiKey;

    @Value("${ABS_FILES_PATH}")
    public String absPath;

    public static final String PATHNAME = "pliki_z_fabryki";
    private static final Set<String> EXCLUDED_FILES = new HashSet<>();

    static {
        EXCLUDED_FILES.add("pliki_z_fabryki/facts");
        EXCLUDED_FILES.add("pliki_z_fabryki/weapons_tests.zip");
        EXCLUDED_FILES.add("pliki_z_fabryki/2024-11-12_report-99");
        EXCLUDED_FILES.add("pliki_z_fabryki/transcriptions");
        EXCLUDED_FILES.add("pliki_z_fabryki/.DS_Store");
    }

    @Override
    public void run() {
        Categories categories = processFilesInDirectory();
        log.info(categories.toString());
        Answer answer = new Answer("kategorie", aidevsApiKey, categories);
        centralaClient.postAnswer(answer);
    }

    private Categories processFilesInDirectory() {
        var dir = new File(PATHNAME);

        List<String> people = new ArrayList<>();
        List<String> hardware = new ArrayList<>();

        if (dir.exists() && dir.isDirectory()) {
            for (File file : dir.listFiles()) {
                if (file.isFile() && !isExcluded(file)) {
                    String fileName = file.getName();
                    String fileType = getFileExtension(fileName);
                    Category category = getCategory(file, fileType);
                    switch (category) {
                        case PERSON: {
                            people.add(file.getName());
                            break;
                        }
                        case HARDWARE: {
                            hardware.add(file.getName());
                            break;
                        }
                        case null, default:
                            break;
                    }
                }
            }
        } else {
            log.warn("Directory does not exist: {}", PATHNAME);
        }
        return new Categories(people.stream().sorted().toList(),
            hardware.stream().sorted().toList());
    }

    @Nullable
    private Category getCategory(File file, String fileType) {
        Category category = null;
        try {
            category = categorize(file, fileType);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return category;
    }

    private String getFileExtension(String fileName) {
        int lastIndexOfDot = fileName.lastIndexOf('.');
        return (lastIndexOfDot == -1) ? "" : fileName.substring(lastIndexOfDot + 1);
    }

    private boolean isExcluded(File file) {
        String path = file.getPath().replace("\\", "/");
        return EXCLUDED_FILES.contains(path);
    }

    private Category categorize(File file, String fileType) throws IOException {

        log.info("Reading text file: {}", file.getName());
        FileType type = FileType.valueOf(fileType.toUpperCase());
        String content = switch (type) {
            case TXT -> new String(Files.readAllBytes(file.toPath()));
            case MP3, PNG -> getTranscription(file.getName());
        };
        Category categoryResponse = assistant.categorize(content);
        log.info("Result of categorisation: {}", categoryResponse);
        return categoryResponse;
    }

    private String getTranscription(String name) {
        File file = new File(absPath + name.replaceAll("\\.[^.]+$", ".txt"));
        if (!file.exists()) {
            throw new NoSuchElementException("Provide transcription for file");
        }
        String text = null;
        try {
            text = new String(Files.readAllBytes(file.toPath()));
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return text;
    }

    record Categories(
        List<String> people,
        List<String> hardware
    ) {
    }
}
