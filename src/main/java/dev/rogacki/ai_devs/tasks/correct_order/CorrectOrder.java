package dev.rogacki.ai_devs.tasks.correct_order;

import dev.rogacki.ai_devs.external.CentralaClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CorrectOrder implements Runnable {

    private final CentralaClient centralaClient;

    @Override
    public void run() {
        ApiDbRequest apiDbRequest = new ApiDbRequest("select * from correct_order");
        String concatenatedLetters = centralaClient.queryApiDb(apiDbRequest).reply().stream()
            .sorted(Comparator.comparingInt(Order::weight))
            .map(Order::letter)
            .collect(Collectors.joining());
        log.info(concatenatedLetters);
    }
}
