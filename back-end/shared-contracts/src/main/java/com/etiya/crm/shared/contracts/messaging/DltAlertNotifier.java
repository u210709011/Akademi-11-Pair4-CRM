package com.etiya.crm.shared.contracts.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;

import java.time.Duration;
import java.util.Map;

/** DLT'ye dusen event'ler icin ortak bildirim noktasi - alert log + opsiyonel webhook. */
@Component
public class DltAlertNotifier {

	private static final Logger alertLog = LoggerFactory.getLogger("ALERTS." + DltAlertNotifier.class.getName());
	private static final Logger log = LoggerFactory.getLogger(DltAlertNotifier.class);

	private final RestClient restClient;

	@Value("${alerting.dlt.webhook-url:}")
	private String webhookUrl;

	public DltAlertNotifier() {
		this.restClient = RestClient.builder()
				.requestFactory(clientHttpRequestFactory())
				.build();
	}

	private static org.springframework.http.client.ClientHttpRequestFactory clientHttpRequestFactory() {
		var factory = new org.springframework.http.client.SimpleClientHttpRequestFactory();
		factory.setConnectTimeout((int) Duration.ofSeconds(2).toMillis());
		factory.setReadTimeout((int) Duration.ofSeconds(2).toMillis());
		return factory;
	}

	/** DLT'ye dusen bir event icin alert tetikler. Webhook cagrisi best-effort'tur; hatasi bu servisi asla dusurmez. */
	public void alert(String service, String listener, String eventType, String exceptionType, String exceptionMessage) {
		alertLog.error(
				"DLT_ALERT service={} listener={} eventType={} exceptionType={} exceptionMessage={} - manuel mudahale gerekebilir",
				service, listener, eventType, exceptionType, exceptionMessage);

		if (!StringUtils.hasText(webhookUrl)) {
			return;
		}

		try {
			restClient.post()
					.uri(webhookUrl)
					.contentType(org.springframework.http.MediaType.APPLICATION_JSON)
					.body(Map.of(
							"text", "DLT alert: %s/%s - %s (%s: %s)".formatted(service, listener, eventType,
									exceptionType, exceptionMessage)))
					.retrieve()
					.toBodilessEntity();
		} catch (Exception ex) {
			log.warn("DLT alert webhook call failed, alert already logged above: {}", ex.getMessage());
		}
	}
}
