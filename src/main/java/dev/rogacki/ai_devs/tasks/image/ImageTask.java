package dev.rogacki.ai_devs.tasks.image;


import dev.langchain4j.data.image.Image;
import dev.langchain4j.model.image.ImageModel;
import dev.langchain4j.model.openai.OpenAiImageModel;
import dev.langchain4j.model.output.Response;
import dev.rogacki.ai_devs.external.CentralaClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.net.URI;

import static dev.langchain4j.model.openai.OpenAiImageModelName.DALL_E_3;


@Slf4j
@Service
@RequiredArgsConstructor
public class ImageTask implements Runnable {

    @Value("${IMAGE_DESCRIPTION_URL}")
    public String imageDescriptionUrl;

    @Value("${OPENAI_API_KEY}")
    public String openAiApiKey;

    private final CentralaClient centralaClient;

    @Override
    public void run() {

        ImageDescriptionResponse imageDescription = RestClient.builder().baseUrl(imageDescriptionUrl).build()
            .get()
            .retrieve()
            .body(ImageDescriptionResponse.class);
        log.info(imageDescription.description());

        ImageModel model = OpenAiImageModel.builder()
            .apiKey(openAiApiKey)
            .modelName(DALL_E_3)
            .logRequests(true)
            .logResponses(true)
            .build();

        Response<Image> response = model.generate("Generate image based on this description: " + imageDescription.description());

        URI generatedImageUrl = response.content().url();
        log.info(generatedImageUrl.toString());
        centralaClient.postAnswer("robotid", generatedImageUrl);
    }
}
