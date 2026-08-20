package com.etiya.crm.productservice.utils;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DiscountPriceCalculatorTest {

	private final DiscountPriceCalculator calculator = new DiscountPriceCalculator();

	@Test
	void calculate_appliesPercentageDiscount() {
		BigDecimal result = calculator.calculate(new BigDecimal("100.00"), new BigDecimal("10"));

		assertThat(result).isEqualByComparingTo("90.00");
	}

	@Test
	void calculate_returnsFullPrice_whenDiscountIsZero() {
		BigDecimal result = calculator.calculate(new BigDecimal("100.00"), BigDecimal.ZERO);

		assertThat(result).isEqualByComparingTo("100.00");
	}

	@Test
	void calculate_roundsToTwoDecimalPlaces() {
		BigDecimal result = calculator.calculate(new BigDecimal("99.99"), new BigDecimal("33"));

		assertThat(result.scale()).isEqualTo(2);
	}
}
