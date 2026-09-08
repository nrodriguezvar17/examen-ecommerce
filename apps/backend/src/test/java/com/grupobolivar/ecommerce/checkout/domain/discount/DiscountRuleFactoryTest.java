package com.grupobolivar.ecommerce.checkout.domain.discount;

import static com.grupobolivar.ecommerce.checkout.domain.discount.DiscountFixtures.TECH;
import static com.grupobolivar.ecommerce.checkout.domain.discount.DiscountFixtures.context;
import static com.grupobolivar.ecommerce.checkout.domain.discount.DiscountFixtures.item;
import static com.grupobolivar.ecommerce.checkout.domain.discount.DiscountFixtures.noCoupons;
import static com.grupobolivar.ecommerce.checkout.domain.discount.DiscountFixtures.rate;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class DiscountRuleFactoryTest {

	private final DiscountConfig config = new DiscountConfig(
			TECH, rate("0.10"), rate("100.00"), rate("0.05"), rate("0.35"));

	@Test
	void buildsAPipelineThatRunsTheThreeRulesInPrecedenceOrder() {
		DiscountPipeline pipeline = DiscountRuleFactory.createPipeline(config, noCoupons());

		DiscountBreakdown breakdown = pipeline.calculate(context(null,
				item(TECH, "800.00", 1),
				item("Papelería", "5.00", 4)));

		// Category first (80.00), then Volume on the reduced total (37.00).
		assertThat(breakdown.lines()).extracting(DiscountLine::type)
				.containsExactly(DiscountType.CATEGORY, DiscountType.VOLUME);
	}
}
