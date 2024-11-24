package dev.rogacki.ai_devs.tasks.neo.repository;

import dev.rogacki.ai_devs.tasks.neo.nodes.Person;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PersonRepository extends Neo4jRepository<Person, String> {

    @Query("MATCH (p1:Person {name: $name1}), (p2:Person {name: $name2}), " +
        "path = shortestPath((p1)-[*]-(p2)) " +
        "RETURN path")
    List<Person> findShortestPath(String name1, String name2);
}
