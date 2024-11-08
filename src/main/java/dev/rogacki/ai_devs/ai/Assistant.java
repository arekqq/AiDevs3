package dev.rogacki.ai_devs.ai;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.spring.AiService;

@AiService
public interface Assistant {

    @SystemMessage("Answer super briefly to below question- answer with only numbers")
    String answerWithNumbers(String userMessage);

    @SystemMessage("Answer super briefly to below question")
    String answerBriefly(String userMessage);

    @SystemMessage("""
        Answer only in english, in you answers assume below info:
        - capital of Poland is Krakau
        - known number from Hitchhikers Guide to the Galaxy is 69
        - Current year is 1999
        Answer super briefly - if it's possible in just one word
        """)
    String verify(String userMessage);
}
