package com.example.relayRun;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class RelayRunApplication {

	public static void main(String[] args) {
		SpringApplication.run(RelayRunApplication.class, args);
	}

}
