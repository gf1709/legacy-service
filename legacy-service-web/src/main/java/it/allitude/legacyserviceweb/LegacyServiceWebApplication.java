package it.allitude.legacyserviceweb;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LegacyServiceWebApplication {

	private static final Logger log = LoggerFactory.getLogger(LegacyServiceWebApplication.class);

	public static void main(String[] args) {

		log.info("Starting application.......");
		SpringApplication.run(LegacyServiceWebApplication.class, args);
	}

}
