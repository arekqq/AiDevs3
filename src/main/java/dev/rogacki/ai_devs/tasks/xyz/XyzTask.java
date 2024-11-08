package dev.rogacki.ai_devs.tasks.xyz;

import dev.rogacki.ai_devs.ai.Assistant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Slf4j
@Service
@RequiredArgsConstructor
public class XyzTask implements Runnable {


    @Value("${XYZ_TASK_URL}")
    public String xyzTaskUrl;
    private final Assistant assistant;

    @Override
    public void run() {
        RestClient xyzRestClient = RestClient.builder().baseUrl(xyzTaskUrl).build();
        String html = xyzRestClient
            .get()
            .retrieve()
            .body(String.class);
        Document doc = Jsoup.parse(html);
        Element paragraph = doc.getElementById("human-question");
        String question = paragraph.childNodes().getLast().toString();
        log.info(question);
        String response = assistant.answerWithNumbers(question);
        log.info(response);
        String form = "username=tester&password=574e112a&answer=" + response;
        ResponseEntity<String> entity = xyzRestClient.post()
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .body(form)
            .retrieve()
            .toEntity(String.class);
        String responseBody = xyzRestClient.get().uri(xyzTaskUrl + entity.getHeaders().getLocation()).retrieve().body(String.class);
        Document responseDoc = Jsoup.parse(responseBody);

        log.info(extractFlag(responseDoc));


    }

    private String extractFlag(Document responseDoc) {
        for (Element element : responseDoc.select("h2")) {
            String text = element.text();
            if (text.startsWith("{{") && text.endsWith("}}")) {
                return text;
            }
        }
        return "NO ELEMENT FOUND";
    }
}
