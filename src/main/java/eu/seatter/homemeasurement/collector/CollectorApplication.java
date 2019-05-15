package eu.seatter.homemeasurement.collector;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Slf4j
public class CollectorApplication implements CommandLineRunner {
	public static void main(String... args) {
		SpringApplication.run(CollectorApplication.class, args).close();
	}

	@Override
	public void run(String... strings) {
	}
}