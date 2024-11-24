package dev.rogacki.ai_devs.tasks.neo.nodes;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.ArrayList;
import java.util.List;

@Node
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Person {

    @Id
    private String id;

    private String name;

    @Relationship(type = "KNOWS", direction = Relationship.Direction.OUTGOING)
    private List<Person> connections = new ArrayList<>();
}
