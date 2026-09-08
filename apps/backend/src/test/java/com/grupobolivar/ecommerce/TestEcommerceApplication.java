package com.grupobolivar.ecommerce;

import org.springframework.boot.SpringApplication;

/**
 * Punto de entrada para levantar la aplicación en local contra el PostgreSQL de
 * Testcontainers (sin necesitar `docker compose up`): ejecutar esta clase desde el IDE.
 */
public class TestEcommerceApplication {

	public static void main(String[] args) {
		SpringApplication.from(EcommerceApplication::main)
				.with(TestcontainersConfiguration.class)
				.run(args);
	}
}
