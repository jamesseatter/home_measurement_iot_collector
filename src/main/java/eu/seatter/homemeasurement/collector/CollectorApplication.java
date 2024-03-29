package eu.seatter.homemeasurement.collector;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(exclude={org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration.class})
@Slf4j
@EnableEncryptableProperties
public class CollectorApplication implements CommandLineRunner {
	public static void main(String... args) {
		SpringApplication.run(CollectorApplication.class, args);
	}

	@Override
	public void run(String... strings) {
		//no code required by spring
	}
}