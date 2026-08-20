package com.etiya.crm.shared.contracts.constants;

/**
 * Bean Validation ({@code @Pattern} vb.) message anahtarlari. Bunlar dogrudan gosterilecek metin
 * DEGIL, sadece JSR-380 message interpolation anahtarlaridir (bkz. kullanim yeri: {@code message =
 * "{" + ValidationMessageKeys.NAME_INVALID + "}"}). shared-contracts duz bir kutuphane JAR'i
 * oldugu icin kendi MessageSource/messages*.properties paketi YOKTUR; bu DTO'lari {@code @Valid}
 * ile calistiran her servis, kendi messages/messages*.properties dosyasinda ayni key'i tanimlamak
 * zorundadir (aksi halde Hibernate Validator anahtari oldugu gibi - suslu parantezlerle -
 * dondurur).
 */
public final class ValidationMessageKeys {

	public static final String NAME_INVALID = "validation.name.invalid";
	public static final String NATIONAL_ID_INVALID = "validation.national-id.invalid";

	private ValidationMessageKeys() {
	}
}
