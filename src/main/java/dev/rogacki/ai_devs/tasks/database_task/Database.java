package dev.rogacki.ai_devs.tasks.database_task;

import dev.rogacki.ai_devs.ai.Assistant;
import dev.rogacki.ai_devs.external.CentralaClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class Database implements Runnable {

    private final Assistant assistant;
    private final CentralaClient centralaClient;

    @Override
    public void run() {

        List<String> tableDescriptions = retrieveTableDescriptions();
        log.info("=== Table descriptions: {}", tableDescriptions);
        String query = assistant.generateQuery(tableDescriptions,
            "które aktywne datacenter (DC_ID) są zarządzane przez pracowników, którzy są na urlopie (is_active=0)");
        log.info("=== Generated query: {}", query);
        var response = centralaClient.  queryApiDb(query, new ParameterizedTypeReference<ApiDbResponse<DataCenters>>() {
        });
        List<Integer> dcIds = response.reply().stream().map(DataCenters::dcId).toList();
        log.info("=== Response dc ids: {}", dcIds);
        centralaClient.postAnswer("database", dcIds);
    }

    private List<String> retrieveTableDescriptions() {
        List<String> tables = List.of("connections", "datacenters", "users");
        List<String> tableDesc = new ArrayList<>();
        for (String table : tables) {
            var response = centralaClient.queryApiDb("show create table %s".formatted(table),
                new ParameterizedTypeReference<ApiDbResponse<TableDescription>>() {
            });
            String description = response.reply().stream()
                .findAny()
                .map(TableDescription::createTable)
                .orElseThrow();
            tableDesc.add(description);
        }
        return tableDesc;
    }
}
