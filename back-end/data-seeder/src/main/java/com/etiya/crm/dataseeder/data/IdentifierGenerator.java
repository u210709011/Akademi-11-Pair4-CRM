package com.etiya.crm.dataseeder.data;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.stereotype.Component;

/**
 * Bu calisma icinde tekil T.C. Kimlik No / GSM / e-posta uretir. nationalId, gercek TC Kimlik No
 * checksum algoritmasiyla uretilir (customer-service'te sadece regex dogrulaniyor olsa da,
 * gercekci gorunmesi icin).
 */
@Component
public class IdentifierGenerator {

	private final Set<String> usedNationalIds = new HashSet<>();
	private final Set<String> usedGsmNumbers = new HashSet<>();
	private final Set<String> usedEmails = new HashSet<>();

	public String nextNationalId() {
		String candidate;
		do {
			candidate = generateNationalId();
		} while (!usedNationalIds.add(candidate));
		return candidate;
	}

	public String nextMobilePhone() {
		String candidate;
		do {
			candidate = "5" + randomDigits(9);
		} while (!usedGsmNumbers.add(candidate));
		return candidate;
	}

	public String homePhone() {
		return "2" + randomDigits(9);
	}

	public String nextEmail(String firstName, String lastName) {
		String base = transliterate(firstName) + "." + transliterate(lastName);
		String candidate = base + "@example.com";
		int suffix = 1;
		while (!usedEmails.add(candidate)) {
			candidate = base + suffix + "@example.com";
			suffix++;
		}
		return candidate;
	}

	private static String generateNationalId() {
		ThreadLocalRandom random = ThreadLocalRandom.current();
		int[] d = new int[9];
		d[0] = random.nextInt(1, 10);
		for (int i = 1; i < 9; i++) {
			d[i] = random.nextInt(0, 10);
		}
		int oddSum = d[0] + d[2] + d[4] + d[6] + d[8];
		int evenSum = d[1] + d[3] + d[5] + d[7];
		int d10 = ((oddSum * 7) - evenSum) % 10;
		if (d10 < 0) {
			d10 += 10;
		}
		int d11 = (oddSum + evenSum + d10) % 10;

		StringBuilder sb = new StringBuilder(11);
		for (int digit : d) {
			sb.append(digit);
		}
		sb.append(d10).append(d11);
		return sb.toString();
	}

	private static String randomDigits(int count) {
		ThreadLocalRandom random = ThreadLocalRandom.current();
		StringBuilder sb = new StringBuilder(count);
		for (int i = 0; i < count; i++) {
			sb.append(random.nextInt(0, 10));
		}
		return sb.toString();
	}

	private static String transliterate(String value) {
		return value.toLowerCase(Locale.forLanguageTag("tr-TR"))
				.replace("ç", "c").replace("ğ", "g").replace("ı", "i")
				.replace("ö", "o").replace("ş", "s").replace("ü", "u");
	}
}
