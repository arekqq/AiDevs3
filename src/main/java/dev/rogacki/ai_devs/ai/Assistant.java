package dev.rogacki.ai_devs.ai;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;
import dev.langchain4j.service.spring.AiService;
import dev.rogacki.ai_devs.tasks.category.Category;

import java.util.List;

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

    @SystemMessage("""
        Zamień dane osobowe na słowo CENZURA - tylko słowa, interpunkcja ma pozostać taka sama. 
        Odpowiedz tylko podmienionym tekstem. 
        W przypadku ciągu danych osobowych podmień to tylko jednym słowem- np:
        Tożsamość podejrzanego: Michał Wiśniewski. Mieszka we Wrocławiu na ul. Słonecznej 20. Wiek: 30 lat.
        Tożsamość podejrzanego: CENZURA. Mieszka we CENZURA na ul. CENZURA. Wiek: CENZURA lat.
        """)
    String censor(String userMessage);

    @SystemMessage("""
        Jesteś asystentem AI. Poniżej znajduje się kontekst w postaci paru wypowiedzi na temat jednej osoby.
        Następnie użytkownik pytanie dotyczące tej osoby.
        Udziel precyzyjnej odpowiedzi, wykorzystując zarówno dostarczony kontekst, jak i swoją wewnętrzną wiedzę.
        Jeśli Twoja wewnętrzna wiedza jest sprzeczna z dostarczonym kontekstem, priorytet ma kontekst dostarczony przez użytkownika.
        Opis po kolei swój proces wnioskowania odpowiedzi.
        
        {{context}}
        """)
    String interrogation(@V("context")List<String> context, @UserMessage String message);

    @SystemMessage("""
        Basing on below text categorize what it to one of two categories.
        PERSON is only for reporting existence of people - only if the existence is positive.
        HARDWARE is for hardware faults only.
        Possible types are: "PERSON" or "HARDWARE" - nothing else. Return category name and nothing else- just one word.
        In case it doesn't apply to both of these categories return NONE.
        Remember, updates are not about hardware.
        If it is about pizza, return NONE.
        """)
    Category categorize(String message);
}
