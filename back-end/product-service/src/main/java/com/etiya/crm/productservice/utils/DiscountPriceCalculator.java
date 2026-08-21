package com.etiya.crm.productservice.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.springframework.stereotype.Component;

/** CampaignOfferingManager'in bes ayri metotta elle tekrarladigi indirimli fiyat hesabi buraya tasindi. */
@Component
public class DiscountPriceCalculator {

	public BigDecimal calculate(BigDecimal totalPrice, BigDecimal discountPct) {
		BigDecimal multiplier = BigDecimal.ONE.subtract(
				discountPct.divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP));
		return totalPrice.multiply(multiplier).setScale(2, RoundingMode.HALF_UP);
	}
}
