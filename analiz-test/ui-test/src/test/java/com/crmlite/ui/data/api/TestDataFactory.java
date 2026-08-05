package com.crmlite.ui.data.api;

import com.crmlite.ui.data.builder.CustomerBuilder;
import com.crmlite.ui.data.model.AddressInfo;
import com.crmlite.ui.data.model.CreatedCustomer;
import com.crmlite.ui.data.model.CustomerData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Testlerin ihtiyac duydugu on kosullari <b>tek satirda</b> kuran yardimci.
 *
 * <p>Kullanim (Faz 4):
 * <pre>{@code
 * CreatedCustomer customer = TestDataFactory.customerWithAddresses(5);   // FR-005 ACC-005
 * CreatedCustomer customer = TestDataFactory.simpleCustomer();           // FR-002 / FR-004
 * }</pre>
 *
 * <p>Kurulan her musteri {@link TestDataCleaner} tarafindan otomatik izlenir.
 */
public final class TestDataFactory {

    private static final Logger log = LoggerFactory.getLogger(TestDataFactory.class);

    private TestDataFactory() {
    }

    /** Tek adresli, gecerli bir musteri. En sik kullanilan on kosul. */
    public static CreatedCustomer simpleCustomer() {
        return CustomerApi.onboard(CustomerBuilder.aValidCustomer().build());
    }

    /** Verilen sablonla musteri olusturur (ozel ad/NAT ID gerektiginde). */
    public static CreatedCustomer customer(CustomerData data) {
        return CustomerApi.onboard(data);
    }

    /**
     * Belirtilen sayida adrese sahip musteri.
     * Onboarding en fazla 5 adres kabul ettigi icin fazlasi ayrica eklenmez.
     */
    public static CreatedCustomer customerWithAddresses(int addressCount) {
        int capped = Math.min(addressCount, 5);
        CreatedCustomer customer = CustomerApi.onboard(
                CustomerBuilder.aValidCustomer().withAddressCount(capped).build());
        AddressApi.ensureAddressCount(customer.custId(), capped);
        return customer;
    }

    /** Adres limiti dolu musteri (FR-003 ACC-010 / FR-005 ACC-005). */
    public static CreatedCustomer customerAtAddressLimit() {
        return customerWithAddresses(5);
    }

    /**
     * FR-002 ACC-007 / ACC-008: sayfalama ve siralama icin ayni soyada sahip
     * {@code count} adet musteri uretir ve ortak soyadi dondurur.
     *
     * <p>Sayfalamanin gorunur olmasi icin {@code count} 10'dan buyuk olmalidir
     * (front-end sayfa boyutu 10).
     */
    public static SearchDataSet searchDataSet(int count) {
        String sharedLastName = "Sayfalama" + com.crmlite.ui.data.builder.TestRunId.value();
        List<CreatedCustomer> customers = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            // Ad alani yalnizca harf kabul ediyor; ayrica siralama (ACC-008) testinin
            // beklenen sirayi dogrulayabilmesi icin alfabetik olarak artan olmali.
            customers.add(CustomerApi.onboard(CustomerBuilder.aValidCustomer()
                    .withFirstName("Kayit" + sortableLetters(i))
                    .withLastName(sharedLastName)
                    .build()));
        }
        log.info("Arama veri seti hazir | {} musteri | ortak soyad={}", count, sharedLastName);
        return new SearchDataSet(sharedLastName, List.copyOf(customers));
    }

    /** Ayni NAT ID cakismasini test etmek icin ikinci bir musteri (FR-003 ACC-005 / FR-004 ACC-007). */
    public static CreatedCustomer customerWithNationalId(String nationalId) {
        return CustomerApi.onboard(CustomerBuilder.aValidCustomer().withNationalId(nationalId).build());
    }

    /** Musteriye ek adres ekler ve olusan adresi dondurur. */
    public static long addAddress(CreatedCustomer customer, String street, String buildingName, String desc) {
        return AddressApi.create(customer.custId(),
                new AddressInfo(AddressInfo.CITY_ANKARA, street, buildingName, desc, false)).id();
    }

    /**
     * Indeksi alfabetik siralamasi indeks sirasiyla ayni olan iki harfe cevirir
     * ({@code 0 -> "aa"}, {@code 1 -> "ab"} ...). Sabit uzunluk sayesinde
     * "Kayitaa" &lt; "Kayitab" &lt; ... siralamasi indeks sirasini korur.
     */
    private static String sortableLetters(int index) {
        return String.valueOf((char) ('a' + (index / 26))) + (char) ('a' + (index % 26));
    }

    /** Arama testleri icin uretilen veri kumesi. */
    public record SearchDataSet(String sharedLastName, List<CreatedCustomer> customers) {

        public int size() {
            return customers.size();
        }

        public CreatedCustomer first() {
            return customers.get(0);
        }
    }
}
