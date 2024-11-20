package dev.rogacki.ai_devs.tasks.vetors;


import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.onnx.allminilml6v2.AllMiniLmL6V2EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import dev.rogacki.ai_devs.external.CentralaClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;

@Slf4j
@Service
@RequiredArgsConstructor
public class VectorsTask implements Runnable {

    private final CentralaClient centralaClient;

    @Value("${OPENAI_API_KEY}")
    public String openAiApiKey;

    @Override
    public void run() {
        String process = process();
        centralaClient.postAnswer("wektory", process);
    }

    private String process() {
        InMemoryEmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>();

        EmbeddingModel embeddingModel = new AllMiniLmL6V2EmbeddingModel();
        var dir = new File("pliki_z_fabryki/do-not-share");

        for (var file : dir.listFiles()) {
            var document = FileSystemDocumentLoader.loadDocument(file.getPath());
            var textSegment = TextSegment.from(document.text(), Metadata.metadata("name", file.getName()));
            Embedding embedding = embeddingModel.embed(textSegment).content();
            embeddingStore.add(embedding, textSegment);
        }

        Embedding queryEmbedding = embeddingModel
            .embed("W raporcie, z którego dnia znajduje się wzmianka o kradzieży prototypu broni?")
            .content();
        EmbeddingSearchRequest searchRequest = EmbeddingSearchRequest.builder()
            .queryEmbedding(queryEmbedding)
            .maxResults(1)
            .build();
        var relevant = embeddingStore.search(searchRequest);
        EmbeddingMatch<TextSegment> match = relevant.matches().getFirst();
        String nameFromMetadata = match.embedded().metadata().getString("name");
        return nameFromMetadata.replaceAll("_", "-").replaceAll(".txt", "");
    }
}
