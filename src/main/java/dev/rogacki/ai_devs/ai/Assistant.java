package dev.rogacki.ai_devs.ai;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.spring.AiService;

@AiService
public interface Assistant {

    @SystemMessage("Answert super briefly to below question- answet with only numbers")
    String chat(String userMessage);
}
