package dev.rogacki.ai_devs.tasks.documents;

import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.rogacki.ai_devs.ai.Assistant;
import dev.rogacki.ai_devs.external.CentralaClient;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.File;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static dev.langchain4j.model.openai.OpenAiChatModelName.GPT_4_O_MINI;

@Slf4j
@Service
@RequiredArgsConstructor
public class DocumentsTask implements Runnable {

    public static final String SYSTEM_MESSAGE = """
                If there is any name return simply first name and last name. If no one is mentione return simply NONE.
                Example:
                Ten teskt wspomina o Janie Pawlastym
                Return: Jan Pawlasty
        """;
    private final Assistant assistant;
    private final CentralaClient centralaClient;

    @Value("${OPENAI_API_KEY}")
    public String openAiApiKey;

    public static final String PATHNAME = "pliki_z_fabryki";
    private static final Set<String> EXCLUDED_FILES = new HashSet<>();
    static {
        EXCLUDED_FILES.add("pliki_z_fabryki/facts");
        EXCLUDED_FILES.add("pliki_z_fabryki/weapons_tests.zip");
        EXCLUDED_FILES.add("pliki_z_fabryki/2024-11-12_report-99");
        EXCLUDED_FILES.add("pliki_z_fabryki/transcriptions");
        EXCLUDED_FILES.add("pliki_z_fabryki/.DS_Store");
    }

    private static final Map<String, String> FACTS_MAP;

    static {
        FACTS_MAP = Map.of(
            "Aleksander Ragowski", "f04.txt",
            "Barbara Zawadzka", "f05.txt",
            "Azazel", "f06.txt",
            "Rafał Bomba", "f08.txt"
            // TODO automatize the search for facts
        );
    }

    @Override
    public void run() {
        Map<String, String> filesAndKeywords = processFilesInDirectory();
        log.info(filesAndKeywords.toString());
        centralaClient.postAnswer("dokumenty", filesAndKeywords);
    }

    private Map<String, String> processFilesInDirectory() {
        var dir = new File(PATHNAME);
        var filesAndKeywords = new HashMap<String, String>();
        ChatLanguageModel model = OpenAiChatModel.builder()
            .apiKey(openAiApiKey)
            .modelName(GPT_4_O_MINI)
            .build();


        for (File file : dir.listFiles()) {
            String extension = StringUtils.getFilenameExtension(file.getName());

            if (!"txt".equalsIgnoreCase(extension)) {
                continue;
            }

            String reportText = readReportTextFromFile(file);
            String name = model.generate(SystemMessage.systemMessage(SYSTEM_MESSAGE), UserMessage.userMessage(reportText)).content().text();
            var keywords = generateKeywords(reportText, file.getName(), name);
            filesAndKeywords.put(file.getName(), keywords);
            log.info("file name: {}", file.getName());
            log.info("person: {}", name);
            log.info("keywords: {}", keywords);
        }


        return filesAndKeywords;
    }

    private String generateKeywords(String reportText, String fileName, String name) {
        var context = "NONE".equals(name) ? "" : readContext(name);
        return assistant.generateKeywords(context, fileName, reportText);
    }

    @SneakyThrows
    private String readContext(String name) {
        var fileName = FACTS_MAP.get(name);
        var file = new File(PATHNAME + "/facts/" + fileName);
        return new String(Files.readAllBytes(file.toPath()));
    }

    @SneakyThrows
    private String readReportTextFromFile(File file) {
        return new String(Files.readAllBytes(file.toPath()));
    }
}
