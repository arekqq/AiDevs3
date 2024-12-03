package dev.rogacki.ai_devs.tasks.algolia;

import com.algolia.api.SearchClient;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.List;

@Slf4j
@Component
public class Algolia implements Runnable {
    @SneakyThrows
    @Override
    public void run() {
        // Fetch sample dataset
        URL url = new URI("https://dashboard.algolia.com/sample_datasets/movie.json").toURL();
        InputStream stream = url.openStream();
        ObjectMapper mapper = new ObjectMapper();
        List<JsonNode> movies = mapper.readValue(stream, new TypeReference<List<JsonNode>>() {
        });
        stream.close();

        // Connect and authenticate with your Algolia app
        SearchClient client = new SearchClient("DVIV2N0MKG", "fec1565c6c087f8f1af93b8c7e7b8ff1");

        // Save records in Algolia index
        client.saveObjects("movies_index", movies);
        client.close();
    }
}
