package dev.rogacki.ai_devs.tasks.neo;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.rogacki.ai_devs.external.CentralaClient;
import dev.rogacki.ai_devs.tasks.database_task.ApiDbResponse;
import dev.rogacki.ai_devs.tasks.neo.nodes.Person;
import dev.rogacki.ai_devs.tasks.neo.repository.PersonRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class Neo implements Runnable {

    private final PersonRepository personRepository;
    private final CentralaClient centralaClient;


    @Override
    public void run() {
//        propagateData();
        List<Person> shortestPath = personRepository.findShortestPath("Rafał", "Barbara");
        String result = shortestPath.stream().map(Person::getName).collect(Collectors.joining(","));
        centralaClient.postAnswer("connections", result);
    }

    private void propagateData() {
        // Fetch user data
        List<PersonResponse> users = centralaClient.queryApiDb("SELECT * FROM users",
            new ParameterizedTypeReference<ApiDbResponse<PersonResponse>>() {}).reply();

        // Fetch relationships
        List<RelationshipResponse> relationships = centralaClient.queryApiDb("SELECT * FROM connections",
            new ParameterizedTypeReference<ApiDbResponse<RelationshipResponse>>() {}).reply();

        // Map users to Person entities
        Map<String, Person> personMap = users.stream()
            .map(this::createPerson)
            .collect(Collectors.toMap(Person::getId, person -> person));

        // Map relationships
        relationships.forEach(rel -> {
            Person user1 = personMap.get(rel.user1Id());
            Person user2 = personMap.get(rel.user2Id());
            if (user1 != null && user2 != null) {
                user1.getConnections().add(user2);
            }
        });

        // Save all entities
        personRepository.saveAll(personMap.values());

        log.info("Data successfully saved to Neo4j.");
    }

    private Person createPerson(PersonResponse user) {
        Person person = new Person();
        person.setId(user.id());
        person.setName(user.username());
        return person;
    }

    // Response records
    record PersonResponse(
        String id,
        String username
    ) {}

    record RelationshipResponse(
        @JsonProperty("user1_id") String user1Id,
        @JsonProperty("user2_id") String user2Id
    ) {}
}
