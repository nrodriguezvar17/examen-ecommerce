package com.grupobolivar.ecommerce.checkout.infrastructure;

import com.grupobolivar.ecommerce.checkout.application.RadicadoGenerator;
import java.time.Clock;
import java.time.ZoneId;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CheckoutConfiguration {

	@Bean
	Clock checkoutClock() {
		return Clock.system(ZoneId.of("America/Bogota"));
	}

	@Bean
	RadicadoGenerator radicadoGenerator(
			@Value("${order.reference.prefix:ORD}") String prefix, Clock clock) {
		return new RadicadoGenerator(prefix, clock);
	}
}
