package com.grupobolivar.ecommerce.checkout.application;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import org.junit.jupiter.api.Test;

class RadicadoGeneratorTest {

	@Test
	void buildsPrefixDashTimestampInTheConfiguredZone() {
		Clock clock = Clock.fixed(
				Instant.parse("2026-09-08T19:30:25.017Z"), ZoneId.of("America/Bogota"));

		// 19:30:25.017 UTC -> 14:30:25.017 in America/Bogota (UTC-5)
		assertThat(new RadicadoGenerator("ORD", clock).next()).isEqualTo("ORD-20260908143025017");
	}

	@Test
	void matchesTheSchemaFormat() {
		String radicado = new RadicadoGenerator("EC", Clock.systemUTC()).next();
		assertThat(radicado).matches("^[A-Z]{2,5}-[0-9]{14,17}$");
	}
}
