package com.gong.modu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class ModuApplication {

	public static void main(String[] args) {
		SpringApplication.run(ModuApplication.class, args);
	}

}