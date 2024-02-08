package com.example.repick;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class RepickApplication {

	public static void main(String[] args) {
		SpringApplication.run(RepickApplication.class, args);
	}

}
