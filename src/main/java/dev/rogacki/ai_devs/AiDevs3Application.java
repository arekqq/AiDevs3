package dev.rogacki.ai_devs;

import dev.rogacki.ai_devs.dto.Answer;
import dev.rogacki.ai_devs.external.TaskClient;
import dev.rogacki.ai_devs.tasks.xyz.Task;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.ResponseEntity;

import java.util.Map;

@SpringBootApplication
public class AiDevs3Application {

	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(AiDevs3Application.class, args);
		Task task = context.getBean(Task.class);
		task.run();
	}

}
