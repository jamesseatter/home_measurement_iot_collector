package eu.seatter.homeheating.collector;

import eu.seatter.homeheating.collector.Application.Collector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CollectorApplication implements CommandLineRunner {

	@Autowired
	private Collector collector;


	@Override
	public void run(String... args) throws Exception {
		collector.execute();
	}

	public static void main(String[] args)  {
		SpringApplication.run(CollectorApplication.class, args);
	}
}
