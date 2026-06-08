package com.example.dailydairy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages = "com.example.dailydairy")
@EnableJpaRepositories(basePackages = "com.example.dailydairy.dairy.repositories")
@EntityScan(basePackages = "com.example.dailydairy.dairy.model")

public class DailydairyApplication {

	public static void main(String[] args) {
		SpringApplication.run(DailydairyApplication.class, args);
		// BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        //System.out.println("Encoded: " + encoder.encode("sk123"));
	}

}
