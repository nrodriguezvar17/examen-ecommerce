package com.grupobolivar.ecommerce.checkout.domain.discount;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.grupobolivar.ecommerce.checkout.domain.coupon.Coupon;
import com.grupobolivar.ecommerce.checkout.domain.coupon.CouponCatalog;
import com.grupobolivar.ecommerce.checkout.domain.model.Cart;
import com.grupobolivar.ecommerce.checkout.domain.model.CartItem;
import com.grupobolivar.ecommerce.checkout.domain.model.Money;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Runs the cumulative discount engine against the shared oracle
 * {@code packages/fixtures/discount-cases.json} (the same table the frontend tests use).
 */
class DiscountPipelineTest {

	private static JsonNode fixtures;
	private static DiscountPipeline pipeline;

	@BeforeAll
	static void loadOracle() throws IOException {
		try (InputStream in = DiscountPipelineTest.class.getResourceAsStream("/discount-cases.json")) {
			fixtures = new ObjectMapper().readTree(in);
		}
		JsonNode rules = fixtures.get("rules");
		DiscountConfig config = new DiscountConfig(
				rules.get("categoryName").asText(),
				rules.get("categoryPercent").decimalValue(),
				rules.get("volumeThreshold").decimalValue(),
				rules.get("volumePercent").decimalValue(),
				rules.get("capPercent").decimalValue());
		pipeline = DiscountRuleFactory.createPipeline(config, couponCatalog(rules));
	}

	static Stream<Arguments> cases() {
		List<Arguments> args = new ArrayList<>();
		for (JsonNode testCase : fixtures.get("cases")) {
			args.add(Arguments.of(testCase.get("name").asText(), testCase));
		}
		return args.stream();
	}

	@ParameterizedTest(name = "{0}")
	@MethodSource("cases")
	void matchesTheOracle(String name, JsonNode testCase) {
		DiscountBreakdown result = pipeline.calculate(contextOf(testCase));
		JsonNode expected = testCase.get("expected");
		JsonNode lines = expected.get("lines");

		assertThat(result.originalTotal().value())
				.isEqualByComparingTo(expected.get("originalTotal").decimalValue());
		assertThat(amountOf(result, DiscountType.CATEGORY))
				.isEqualByComparingTo(lines.get("CATEGORY").decimalValue());
		assertThat(amountOf(result, DiscountType.VOLUME))
				.isEqualByComparingTo(lines.get("VOLUME").decimalValue());
		assertThat(amountOf(result, DiscountType.COUPON))
				.isEqualByComparingTo(lines.get("COUPON").decimalValue());
		assertThat(result.totalDiscount().value())
				.isEqualByComparingTo(expected.get("totalDiscount").decimalValue());
		assertThat(result.effectiveRate())
				.isEqualByComparingTo(expected.get("effectiveRate").decimalValue());
		assertThat(result.finalTotal().value())
				.isEqualByComparingTo(expected.get("finalTotal").decimalValue());
		assertThat(result.capReached()).isEqualTo(expected.get("capReached").asBoolean());
	}

	private static DiscountContext contextOf(JsonNode testCase) {
		List<CartItem> items = new ArrayList<>();
		for (JsonNode item : testCase.get("items")) {
			items.add(new CartItem(
					item.get("productId").asLong(),
					item.get("name").asText(),
					Money.of(item.get("unitPrice").decimalValue()),
					item.get("quantity").asInt(),
					item.get("category").asText()));
		}
		JsonNode coupon = testCase.get("coupon");
		return new DiscountContext(new Cart(items), coupon.isNull() ? null : coupon.asText());
	}

	private static BigDecimal amountOf(DiscountBreakdown breakdown, DiscountType type) {
		return breakdown.lines().stream()
				.filter(line -> line.type() == type)
				.map(line -> line.amount().value())
				.findFirst()
				.orElse(BigDecimal.ZERO);
	}

	private static CouponCatalog couponCatalog(JsonNode rules) {
		JsonNode percent = rules.get("couponPercent");
		JsonNode active = rules.get("couponActive");
		return code -> {
			if (code == null || !percent.has(code)) {
				return Optional.empty();
			}
			if (!active.has(code) || !active.get(code).asBoolean()) {
				return Optional.empty();
			}
			return Optional.of(new Coupon(code, percent.get(code).decimalValue(), true));
		};
	}
}
