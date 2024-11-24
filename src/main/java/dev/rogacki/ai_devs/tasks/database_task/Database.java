package dev.rogacki.ai_devs.tasks.database_task;

import dev.rogacki.ai_devs.external.CentralaClient;
import dev.rogacki.ai_devs.tasks.correct_order.ApiDbRequest;
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

    private final CentralaClient centralaClient;

    @Override
    public void run() {

        List<String> tableDescriptions = retrieveTableDescriptions();
        log.info(tableDescriptions.toString());
    }

    private List<String> retrieveTableDescriptions() {
        List<String> tables = List.of("connections", "datacenters", "users");
        List<String> tableDesc = new ArrayList<>();
        for (String table : tables) {
            ApiDbRequest apiDbRequest = new ApiDbRequest("show create table %s".formatted(table));
            var response = centralaClient.queryApiDb(apiDbRequest, new ParameterizedTypeReference<ApiDbResponse<TableDescription>>() {
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
