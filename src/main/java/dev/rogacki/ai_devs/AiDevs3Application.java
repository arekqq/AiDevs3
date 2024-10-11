package dev.rogacki.ai_devs;

import dev.rogacki.ai_devs.dto.Answer;
import dev.rogacki.ai_devs.external.TaskClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.ResponseEntity;

import java.util.Map;

@SpringBootApplication
public class AiDevs3Application {

	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(AiDevs3Application.class, args);
		TaskClient taskClient = context.getBean(TaskClient.class);
		String data = taskClient.getData();
		String[] split = data.split("\n");
		String apiKey = System.getenv("AIDEVS_API_KEY");
		ResponseEntity<Map<String, String>> mapResponseEntity = taskClient.postAnswer(new Answer("POLIGON", apiKey, split));
		System.out.println(data);
	}

}
