package dev.rogacki.ai_devs.ai;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;
import dev.langchain4j.service.spring.AiService;
import dev.rogacki.ai_devs.tasks.category.Category;

import java.util.List;
import java.util.Set;

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

    @SystemMessage("""
        Na podstawie kontekstu poniżej, wygeneruj listę słów kluczowych (metadanych na temat danego raportu
        z wiadomości użytkownika. Odpowiedź zwróć w postaci słów w mianowniku oddzielonych przecinkiem.
        Jeśli ktoś zna języki programowania, umieść je w metadanych.
        Jako metadane dołącz także nazwę sektora na podstawie nazwy pliku: {{fileName}} - np. 2024-11-12_report-07-sektor_C4.txt to "Sektor C4"
        {{context}}
        """)
    String generateKeywords(@V("context") String context, @V("fileName") String fileName, @UserMessage String reportText);

    @SystemMessage("""
        Jesteś ekspertem baz danych specjalizującym się w MySql
        Na podstawie poniższych definicji tabel przygotu zapytanie które odpowie na pytanie które zada użytkownik.
        Odpowiedz w formie czystego sql, bez żadnych znaczników formatowania, np:
        pytanie: zwróć wszystkich użytkowników
        odpowiedź: select * from users;
        
        Definicje tabel:
        {{tableDefinitions}}
        """)
    String generateQuery(@V("tableDefinitions") List<String> tableDefinitions, @UserMessage String reportText);

    @SystemMessage("""
        Zwróć z poniższej notatki tylko imiona w formie mianownika, pisane dużymi literami.
        Polskie znaki zamien na ich odpowiedniki- np. Ł to L, Ą to A itp.
        Pomiń imię Barbara
        Np:
        Input: Jan z marią i Rafałem poszli na spacer.
        Output: JAN,MARIA,RAFAL
        """)
    Set<String> getNames(String barbaraNote);

    @SystemMessage("""
        Zmapuj opis użytkownika na koordynaty dwuwymiarowej mapy o rozmiarze 4x4.
        W odpowiedz zwróć wektor ruchu po tej mapie, zakładając że pozycja początkowa to 0x0, czyli maksymalna wartość to 3. Zakładamy że początek znajduje się po lewej u góry, współrzędne rosną w prawo i w dół.
        Podaj współrzędne według wzorca: poziome, pionowe
        Odpowiedź zawsze musi zawierać dwie wartości
        Przykłady:
        Użytkownik: Idę dwa kroki w prawo i jeden w dół
        Odpowiedź: 2,1
        
        Użytkownik: Idę 3 kroki w dół i cofam się jeden i na maxa w prawo
        Odpowiedź: 3,2
        """)
    String getCoord(String message);

    @SystemMessage("""
        Odpowiedz na pytanie używając kontesktu.
        Kontest:
        {{match}}
        """)
    String answerWithEmbedding(@UserMessage String question, @V("match") String match);
}
