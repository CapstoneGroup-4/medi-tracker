package edu.capstone4.userserver;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan("edu.capstone4.userserver.properties")
public class MediTrackerUserServerApplication {

	public static void main(String[] args) {
		Dotenv dotenv = Dotenv.configure()
				.directory("./")
				.ignoreIfMalformed()
				.ignoreIfMissing()
				.load();
		String dbPassword = dotenv.get("MYSQL_PASS");
		String redisPassword = dotenv.get("REDIS_PASS");
		System.setProperty("spring.datasource.password", dbPassword);
		System.setProperty("spring.data.redis.password", redisPassword);
		SpringApplication.run(MediTrackerUserServerApplication.class, args);
	}
}
