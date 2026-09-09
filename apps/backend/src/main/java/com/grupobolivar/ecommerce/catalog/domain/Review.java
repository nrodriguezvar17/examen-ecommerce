package com.grupobolivar.ecommerce.catalog.domain;

import java.time.Instant;

/**
 * A single product review, as the domain sees it (plain data, no JPA).
 *
 * @param id           review identifier
 * @param rating       score 1..5
 * @param title        short headline (may be {@code null})
 * @param comment      free text (may be {@code null})
 * @param reviewerName display name of the author
 * @param createdAt    when it was posted
 */
public record Review(
		long id,
		int rating,
		String title,
		String comment,
		String reviewerName,
		Instant createdAt) {
}
