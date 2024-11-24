package dev.rogacki.ai_devs;

import dev.rogacki.ai_devs.tasks.database_task.Database;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class AiDevs3Application {

	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(AiDevs3Application.class, args);
		var task = context.getBean(Database.class);
		task.run();
		context.close();
	}

}
