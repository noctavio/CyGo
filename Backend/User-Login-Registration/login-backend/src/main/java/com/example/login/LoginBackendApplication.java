package com.example.login;

import org.springframework.boot.SpringApplication;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication(scanBasePackages = "com.example.login.*")
@EnableJpaRepositories(basePackages = {"com.example.login.*"})
@EntityScan("com.example.login.*")
@ComponentScan("com.example.login.*")
public class LoginBackendApplication {

	public static void main(String[] args) throws Exception {
		SpringApplication.run(LoginBackendApplication.class, args);
	}
}