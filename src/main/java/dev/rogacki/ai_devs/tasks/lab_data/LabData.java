package dev.rogacki.ai_devs.tasks.lab_data;

import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.request.ChatRequest;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.rogacki.ai_devs.external.CentralaClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class LabData implements Runnable {

    private static final String GPT_4_FINE_TUNED_FOR_LAB_DATA =
        "ft:gpt-4o-mini-2024-07-18:personal:lab-data:AYgzpqht\n";
    private final CentralaClient centralaClient;

    @Value("${OPENAI_API_KEY}")
    public String openAiApiKey;

    @Override
    public void run() {
        var model = OpenAiChatModel.builder()
            .apiKey(openAiApiKey)
            .modelName(GPT_4_FINE_TUNED_FOR_LAB_DATA)
            .build();

        var verifyInput = Map.of(
            "01", "12,100,3,39",
            "02", "-41,75,67,-25",
            "03", "78,38,65,2",
            "04", "5,64,67,30",
            "05", "33,-21,16,-72",
            "06", "99,17,69,61",
            "07", "17,-42,-65,-43",
            "08", "57,-83,-54,-43",
            "09", "67,-55,-6,-32",
            "10", "-20,-23,-2,44");
        var verified = new ArrayList<String>();
        SystemMessage systemMessage = new SystemMessage("Klasyfikuj wyniki");
        verifyInput.forEach((key, value) -> {
            ChatRequest request = ChatRequest.builder()
                .messages(systemMessage, new UserMessage(value))
                .build();
            ChatResponse chatResponse = model.chat(request);
            String text = chatResponse.aiMessage().text();
            if ("CORRECT".equals(text)) {
                verified.add(key);
            }
        });
        centralaClient.postAnswer("research", verified);

    }
}
