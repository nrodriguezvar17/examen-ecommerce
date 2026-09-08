package com.grupobolivar.ecommerce;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

/**
 * Provee un PostgreSQL real (misma imagen que producción) para los tests de integración.
 * {@code @ServiceConnection} conecta el datasource de Spring al contenedor sin propiedades
 * manuales. Los tests unitarios del motor de descuentos no usan esto: son Java puro.
 */
@TestConfiguration(proxyBeanMethods = false)
public class TestcontainersConfiguration {

	@Bean
	@ServiceConnection
	PostgreSQLContainer postgresContainer() {
		return new PostgreSQLContainer(DockerImageName.parse("postgres:16-alpine"));
	}
}
