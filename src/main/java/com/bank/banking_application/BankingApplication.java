package com.bank.banking_application;

import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(
		info = @Info(
				title = "Banking Application",
				description = "Spring Boot backend APIs for managing bank accounts.",
				version = "v1.0"
		),
		externalDocs = @ExternalDocumentation(
				description = "Full source code and documentation available on GitHub",
				url = "https://github.com/DenisaFleancu26/Banking-Application---Spring-Boot"
		)
)
public class BankingApplication {

	public static void main(String[] args) {
		SpringApplication.run(BankingApplication.class, args);
	}

}
