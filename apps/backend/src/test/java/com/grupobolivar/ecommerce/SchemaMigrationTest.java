package com.grupobolivar.ecommerce;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import javax.sql.DataSource;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Verifies that Flyway applied the schema (V1), the view (V2) and the reference and catalog
 * seed data (V3, V4) against the real PostgreSQL provided by Testcontainers.
 */
@SpringBootTest
@Import(TestcontainersConfiguration.class)
class SchemaMigrationTest {

	private static final String[] BASE_TABLES = {
		"categories", "products", "product_details", "product_reviews", "coupons",
		"customers", "order_statuses", "orders", "order_lines", "order_discounts",
		"order_status_history"
	};

	@Autowired
	DataSource dataSource;

	private JdbcTemplate jdbc;

	private JdbcTemplate jdbc() {
		if (jdbc == null) {
			jdbc = new JdbcTemplate(dataSource);
		}
		return jdbc;
	}

	@Test
	void createsTheElevenTablesAndTheView() {
		Integer tables = jdbc().queryForObject(
				"select count(*) from information_schema.tables "
						+ "where table_schema = 'public' and table_type = 'BASE TABLE' "
						+ "and table_name = any (?)",
				Integer.class, (Object) BASE_TABLES);
		assertThat(tables).isEqualTo(BASE_TABLES.length);

		Integer view = jdbc().queryForObject(
				"select count(*) from information_schema.views "
						+ "where table_schema = 'public' and table_name = 'product_rating_summary'",
				Integer.class);
		assertThat(view).isEqualTo(1);
	}

	@Test
	void seedsTheReferenceData() {
		assertThat(jdbc().queryForObject("select count(*) from order_statuses", Integer.class)).isEqualTo(4);
		assertThat(jdbc().queryForObject(
				"select is_final from order_statuses where code = 'ENTREGADO'", Boolean.class)).isTrue();

		assertThat(jdbc().queryForObject("select count(*) from categories", Integer.class)).isEqualTo(5);
		assertThat(jdbc().queryForObject(
				"select discount_rate from categories where name = 'Tecnología'", BigDecimal.class))
				.isEqualByComparingTo("0.1000");

		assertThat(jdbc().queryForObject(
				"select active from coupons where code = 'WELCOME2026'", Boolean.class)).isTrue();
		assertThat(jdbc().queryForObject(
				"select active from coupons where code = 'BLACKFRIDAY2025'", Boolean.class)).isFalse();
	}

	@Test
	void seedsTheCatalogAndCustomers() {
		assertThat(jdbc().queryForObject("select count(*) from products", Integer.class)).isEqualTo(10);
		assertThat(jdbc().queryForObject(
				"select count(*) from products p join categories c on c.id = p.category_id "
						+ "where c.name = 'Tecnología'", Integer.class)).isEqualTo(5);

		// Every product has its 1:1 detail row.
		assertThat(jdbc().queryForObject(
				"select count(*) from products p left join product_details d on d.product_id = p.id "
						+ "where d.product_id is null", Integer.class)).isZero();

		assertThat(jdbc().queryForObject("select count(*) from product_reviews", Integer.class))
				.isGreaterThanOrEqualTo(10);

		assertThat(jdbc().queryForObject(
				"select count(*) from customers where username in ('ana', 'carlos')", Integer.class))
				.isEqualTo(2);
	}

	@Test
	void ratingSummaryViewComputesTheAverage() {
		// Laptop: reviews 5 and 4 -> average 4.50, count 2.
		BigDecimal average = jdbc().queryForObject(
				"select s.rating_average from product_rating_summary s "
						+ "join products p on p.id = s.product_id where p.sku = 'TEC-LAP-014'",
				BigDecimal.class);
		assertThat(average).isEqualByComparingTo("4.50");
	}
}
