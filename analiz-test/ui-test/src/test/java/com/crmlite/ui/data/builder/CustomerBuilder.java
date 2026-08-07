package com.crmlite.ui.data.builder;

import com.crmlite.ui.core.utils.DateUtil;
import com.crmlite.ui.core.utils.NationalIdGenerator;
import com.crmlite.ui.data.model.AddressInfo;
import com.crmlite.ui.data.model.ContactInfo;
import com.crmlite.ui.data.model.CustomerData;
import com.crmlite.ui.data.model.Gender;
import com.crmlite.ui.data.model.IndividualInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Test musterisi uretici.
 *
 * <p>Varsayilan olarak <b>tamamen gecerli</b> ve <b>benzersiz</b> bir musteri uretir;
 * negatif senaryolar tek satirda tureti:
 * <pre>{@code
 * CustomerBuilder.aValidCustomer().withoutLastName().build();
 * CustomerBuilder.aValidCustomer().withBirthDate(DateUtil.tomorrow()).build();
 * }</pre>
 *
 * <p>Benzersizlik: her ornek yeni bir Nationality ID ve kosuya ozel bir ek alir
 * ({@link TestRunId}); boylece paralel kosumlar ve tekrar calistirmalar catismaz.
 */
public final class CustomerBuilder {

    private static final AtomicInteger SEQUENCE = new AtomicInteger();

    private String firstName;
    private String middleName = "Orta";
    private String lastName;
    private String birthDate = "15/06/1990";
    private Integer genderId = Gender.MALE.id();
    private String motherName = "Anne";
    private String fatherName = "Baba";
    private String nationalId = NationalIdGenerator.next();

    private String email;
    private String mobilePhone;
    // Home Phone kurali: 2 ile baslar, 10 hane (FR-003 tablosu; UI'da /^2\d{9}$/).
    // Alan kilit oneme sahip: gecersiz bir varsayilan, "mutlu yol" testlerinde
    // Create butonunu sessizce pasif birakir.
    private String homePhone = "2121234567";
    // Fax'in basamak kurali yok, yalnizca en fazla 11 hane.
    private String fax = "2129876543";

    private final List<AddressInfo> addresses = new ArrayList<>();

    private CustomerBuilder() {
        int sequence = SEQUENCE.incrementAndGet();
        // Ad alanlari yalnizca harf kabul ediyor ("Name should contain letters only." -> 400),
        // bu yuzden sira numarasi da harfe cevrilir.
        String suffix = TestRunId.value() + TestRunId.toLetters(sequence);
        this.firstName = "Test" + suffix;
        this.lastName = "Soyad" + suffix;
        this.email = ("crm.test." + suffix + "@example.com").toLowerCase();
        this.mobilePhone = randomMobile();
        this.addresses.add(AddressBuilder.aValidAddress(0).build());
    }

    public static CustomerBuilder aValidCustomer() {
        return new CustomerBuilder();
    }

    // --- Demografik ---

    public CustomerBuilder withFirstName(String value) {
        this.firstName = value;
        return this;
    }

    public CustomerBuilder withMiddleName(String value) {
        this.middleName = value;
        return this;
    }

    public CustomerBuilder withLastName(String value) {
        this.lastName = value;
        return this;
    }

    public CustomerBuilder withBirthDate(String ddMMyyyy) {
        this.birthDate = ddMMyyyy;
        return this;
    }

    public CustomerBuilder withGender(Gender gender) {
        this.genderId = gender.id();
        return this;
    }

    public CustomerBuilder withMotherName(String value) {
        this.motherName = value;
        return this;
    }

    public CustomerBuilder withFatherName(String value) {
        this.fatherName = value;
        return this;
    }

    public CustomerBuilder withNationalId(String value) {
        this.nationalId = value;
        return this;
    }

    // --- Kontakt ---

    public CustomerBuilder withEmail(String value) {
        this.email = value;
        return this;
    }

    public CustomerBuilder withMobilePhone(String value) {
        this.mobilePhone = value;
        return this;
    }

    public CustomerBuilder withHomePhone(String value) {
        this.homePhone = value;
        return this;
    }

    public CustomerBuilder withFax(String value) {
        this.fax = value;
        return this;
    }

    // --- Adresler ---

    public CustomerBuilder withAddresses(List<AddressInfo> value) {
        this.addresses.clear();
        this.addresses.addAll(value);
        return this;
    }

    public CustomerBuilder addAddress(AddressInfo address) {
        this.addresses.add(address);
        return this;
    }

    /** FR-003 ACC-010 / FR-005 ACC-005: adres limiti testleri icin n adet adres. */
    public CustomerBuilder withAddressCount(int count) {
        this.addresses.clear();
        for (int i = 0; i < count; i++) {
            this.addresses.add(AddressBuilder.aValidAddress(i).build());
        }
        return this;
    }

    /** FR-003 ACC-011: adressiz — API bunu 400 ile reddetmelidir. */
    public CustomerBuilder withoutAddress() {
        this.addresses.clear();
        return this;
    }

    // --- Negatif varyantlar (FR-003 / FR-004 validasyon tablolari) ---

    public CustomerBuilder withoutFirstName() {
        this.firstName = "";
        return this;
    }

    public CustomerBuilder withoutLastName() {
        this.lastName = "";
        return this;
    }

    public CustomerBuilder withoutBirthDate() {
        this.birthDate = "";
        return this;
    }

    public CustomerBuilder withoutGender() {
        this.genderId = null;
        return this;
    }

    public CustomerBuilder withoutNationalId() {
        this.nationalId = "";
        return this;
    }

    public CustomerBuilder withoutEmail() {
        this.email = "";
        return this;
    }

    public CustomerBuilder withoutMobilePhone() {
        this.mobilePhone = "";
        return this;
    }

    /** Sinir degeri: gelecek tarih reddedilmelidir. */
    public CustomerBuilder withFutureBirthDate() {
        return withBirthDate(DateUtil.tomorrow());
    }

    /** Sinir degeri: 01/01/1900 oncesi reddedilmelidir. */
    public CustomerBuilder withTooOldBirthDate() {
        return withBirthDate(DateUtil.justBeforeMinBirthDate());
    }

    /** Sinir degeri: 01/01/1900 kabul edilmelidir. */
    public CustomerBuilder withEarliestValidBirthDate() {
        return withBirthDate(DateUtil.minValidBirthDate());
    }

    /** "Maks 50 karakter" sinir degeri icin ad uretir. */
    public CustomerBuilder withFirstNameOfLength(int length) {
        return withFirstName("A".repeat(Math.max(0, length)));
    }

    public CustomerBuilder withLastNameOfLength(int length) {
        return withLastName("B".repeat(Math.max(0, length)));
    }

    // --- Insa ---

    public IndividualInfo buildIndividual() {
        return new IndividualInfo(firstName, middleName, lastName, birthDate,
                genderId, motherName, fatherName, nationalId);
    }

    public ContactInfo buildContact() {
        return new ContactInfo(email, mobilePhone, homePhone, fax);
    }

    public CustomerData build() {
        return new CustomerData(buildIndividual(), List.copyOf(addresses), buildContact());
    }

    /** Kurala uygun (10 hane, 5 ile baslayan) rastgele cep telefonu. */
    private static String randomMobile() {
        long unique = Math.abs(System.nanoTime() % 100_000_000L);
        return "5" + String.format("%09d", unique).substring(0, 9);
    }
}
