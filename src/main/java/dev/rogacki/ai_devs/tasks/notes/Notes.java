package dev.rogacki.ai_devs.tasks.notes;

import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.onnx.allminilml6v2.AllMiniLmL6V2EmbeddingModel;
import dev.langchain4j.model.openai.OpenAiTokenizer;
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import dev.rogacki.ai_devs.ai.Assistant;
import dev.rogacki.ai_devs.external.CentralaClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.nio.file.Path;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static dev.langchain4j.model.openai.OpenAiChatModelName.GPT_4_O;

@Slf4j
@Component
@RequiredArgsConstructor
public class Notes implements Runnable {

    @Value("${NOTES_PATH}")
    public String notesPath;
    @Value("${PATH_FILE_NAME}")
    private String pathFileName;

    private final LlamaApiService llamaApiService;
    private final InMemoryEmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>();
    private final EmbeddingModel embeddingModel = new AllMiniLmL6V2EmbeddingModel();
    private final CentralaClient centralaClient;
    private final Assistant assistant;

    @Override
    public void run() {
//        Path filePath = Path.of(pathFileName);
//        String markdown = processDocument(filePath).block();

        embed();
        Map<String, String> notesQuestions = centralaClient.getNotesQuestions();
        Map<String, String> responses = new HashMap<>();
        notesQuestions.forEach((key, value) -> {
            var match = getMatch(value);
            log.info(match);
            var response = assistant.answerWithEmbedding(value, match);
            responses.put(key, response);
        });
        centralaClient.postAnswer("notes", responses);
    }

    private String getMatch(String value) {
        Embedding queryEmbedding = embeddingModel
            .embed(value)
            .content();
        EmbeddingSearchRequest searchRequest = EmbeddingSearchRequest.builder()
            .queryEmbedding(queryEmbedding)
            .maxResults(1)
            .build();
        var relevant = embeddingStore.search(searchRequest);
        return relevant.matches().getFirst().embedded().text();
    }

    private void embed() {
        var document = FileSystemDocumentLoader.loadDocument(notesPath);
        DocumentSplitter splitter = DocumentSplitters.recursive(200, 50,
            new OpenAiTokenizer(GPT_4_O));
        List<TextSegment> segments = splitter.split(document);
        List<Embedding> embeddings = embeddingModel.embedAll(segments).content();
        embeddingStore.addAll(embeddings, segments);
    }

    public Mono<String> processDocument(Path filePath) {
        return llamaApiService.uploadDocument(filePath)
            .flatMap(jobId ->
                llamaApiService.checkStatus(jobId)
                    .repeatWhenEmpty(repeat -> repeat.delayElements(Duration.ofSeconds(5)))
                    .filter("SUCCESS"::equalsIgnoreCase)
                    .flatMap(status -> llamaApiService.downloadResult(jobId))
                    .map(result -> result.get("markdown"))
            );
    }
}
