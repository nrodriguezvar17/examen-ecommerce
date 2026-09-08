package com.grupobolivar.ecommerce;

import static org.assertj.core.api.Assertions.assertThat;

import javax.sql.DataSource;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Verifica que Flyway aplicó el esquema (V1) y el seed del catálogo (V2) sobre el
 * PostgreSQL real de Testcontainers.
 */
@SpringBootTest
@Import(TestcontainersConfiguration.class)
class SchemaMigrationTest {

	@Autowired
	DataSource dataSource;

	@Test
	void aplicaEsquemaYSeedDelCatalogo() {
		JdbcTemplate jdbc = new JdbcTemplate(dataSource);

		Integer tablas = jdbc.queryForObject(
				"select count(*) from information_schema.tables "
						+ "where table_schema = 'public' and table_name in ('products', 'orders', 'order_lines')",
				Integer.class);
		assertThat(tablas).isEqualTo(3);

		Integer productos = jdbc.queryForObject("select count(*) from products", Integer.class);
		assertThat(productos).isEqualTo(8);

		Integer tecnologia = jdbc.queryForObject(
				"select count(*) from products where category = 'Tecnología'", Integer.class);
		assertThat(tecnologia).isEqualTo(5);
	}
}
