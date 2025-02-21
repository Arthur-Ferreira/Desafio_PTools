package com.cyborgdata.demo;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@EnableAsync
@OpenAPIDefinition(
		info = @Info(
				title = "Pending Processing API",
				version = "1.0",
				description = "API for handling pending data processing"
		)
)
public class CyborgDataApplication {

	public static void main(String[] args) {
		SpringApplication.run(CyborgDataApplication.class, args);
	}

}
