package com.grupobolivar.ecommerce;

import org.springframework.boot.SpringApplication;

/**
 * Entry point to run the application locally against the Testcontainers PostgreSQL (no
 * {@code docker compose up} needed): run this class from the IDE.
 */
public class TestEcommerceApplication {

	public static void main(String[] args) {
		SpringApplication.from(EcommerceApplication::main)
				.with(TestcontainersConfiguration.class)
				.run(args);
	}
}
