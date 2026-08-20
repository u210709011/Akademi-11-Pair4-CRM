package com.etiya.crm.dataseeder;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "seeder")
public record SeederProperties(
		String gatewayBaseUrl,
		String keycloakTokenUrl,
		String keycloakClientId,
		String keycloakClientSecret,
		String keycloakUsername,
		String keycloakPassword,
		int customerCount,
		double extraBillingAccountRatio,
		double orderAttemptRatio,
		double orderFinishRatio,
		double orderCancelRatio,
		double secondOfferingRatio) {
}
