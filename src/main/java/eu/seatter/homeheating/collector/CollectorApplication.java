package eu.seatter.homeheating.collector;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Slf4j
public class CollectorApplication implements CommandLineRunner {
	public static void main(String... args) {
		SpringApplication.run(CollectorApplication.class, args);
	}

	@Override
	public void run(String... strings) {
		log.info("Loading data...");

	}

//	@Override
//	@PostConstruct
//	public void run(String... args) throws Exception {
//		log.info("Application Main");
//		log.info("Starting service");
//		IOTServiceImpl readerService = new IOTServiceImpl();
//		if(readerService == null) {
//			System.out.println("ERROR");
//		}
//		readerService.run();
//		log.info("Ending service");
//	}
}