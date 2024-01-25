package com.project.cardvisor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@ComponentScan(basePackages={"com.project.cardvisor"}) 
@SpringBootApplication
public class CardVisorApplication {

	public static void main(String[] args) {
		SpringApplication.run(CardVisorApplication.class, args);
	}

}
