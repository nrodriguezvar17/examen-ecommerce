package com.grupobolivar.ecommerce.checkout.application;

import java.time.Clock;
import java.time.format.DateTimeFormatter;

/**
 * Builds the order radicado: {@code <PREFIX>-<yyyyMMddHHmmssSSS>}, e.g.
 * {@code ORD-20260908143025017}. The number is the creation instant as a natural number,
 * so it matches the {@code check (radicado ~ '^[A-Z]{2,5}-[0-9]{14,17}$')} of the schema.
 */
public class RadicadoGenerator {

	private static final DateTimeFormatter NUMBER = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");

	private final String prefix;
	private final Clock clock;

	public RadicadoGenerator(String prefix, Clock clock) {
		this.prefix = prefix;
		this.clock = clock;
	}

	public String next() {
		return prefix + "-" + NUMBER.format(clock.instant().atZone(clock.getZone()));
	}
}
