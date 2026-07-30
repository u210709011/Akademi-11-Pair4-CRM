package com.etiya.crm.shared.contracts.feign;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import feign.Retryer;

/**
 * @EnableFeignClients(defaultConfiguration = DefaultFeignRetryConfig.class) ile her Feign
 * client'ina GetOnlyRetryer'i tek yerden baglamak icin. Ayrica bkz. shared-contracts'taki
 * error paketi (AbstractDownstreamExceptionHandler) - circuit breaker / timeout ayarlari ise
 * her servisin kendi (Config Server'daki) application.yml'inde tanimlidir.
 */
@Configuration
public class DefaultFeignRetryConfig {

	@Bean
	public Retryer retryer() {
		return new GetOnlyRetryer();
	}
}
