package dev.rogacki.ai_devs.tasks.loop;

import dev.rogacki.ai_devs.ai.Assistant;
import dev.rogacki.ai_devs.external.CentralaClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class Loop implements Runnable {

    public static final String BARBARA = "BARBARA";
    private final Assistant assistant;
    private final CentralaClient centralaClient;

    @Override
    public void run() {

        var barbaraNote = centralaClient.retrieveNote();

        Queue<String> peopleQueue = assistant.getNames(barbaraNote).stream()
            .map(String::trim)
            .distinct()
            .collect(Collectors.toCollection(LinkedList::new)); // Kolejka do przetwarzania ludzi
        Set<String> processedPeople = new HashSet<>();              // Ludzie już przetworzeni
        Set<String> processedCities = new HashSet<>();              // Miasta już przetworzone

        while (!peopleQueue.isEmpty()) {
            String currentPerson = peopleQueue.poll(); // Pobierz osobę z kolejki
            if (processedPeople.contains(currentPerson)) {
                continue; // Omiń, jeśli już przetworzono
            }

            // Pobierz miasta odwiedzone przez bieżącą osobę
            Set<String> citiesVisited = centralaClient.peopleSearch(currentPerson.trim());
            for (String city : citiesVisited) {
                if (processedCities.contains(city)) {
                    continue; // Omiń, jeśli miasto już przetworzono
                }

                // Pobierz ludzi w mieście
                Set<String> peopleInCity = centralaClient.citySearch(city);
                if (peopleInCity.contains(BARBARA)) {
                    centralaClient.postAnswer("", city); // Znaleziono miasto Barbary
                }

                // Dodaj nowych ludzi do kolejki
                for (String person : peopleInCity) {
                    if (!processedPeople.contains(person)) {
                        peopleQueue.offer(person); // Dodaj tylko nieprzetworzonych
                    }
                }
                processedCities.add(city); // Zaznacz miasto jako przetworzone
            }
            processedPeople.add(currentPerson); // Zaznacz osobę jako przetworzoną
        }
    }
}
