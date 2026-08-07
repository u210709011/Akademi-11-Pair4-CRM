package com.crmlite.ui.data.api;

import com.crmlite.ui.core.report.AllureAttachment;
import com.crmlite.ui.data.builder.TestRunId;
import com.crmlite.ui.data.model.CreatedCustomer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Kosum sonunda test verisini temizler.
 *
 * <p><b>Onemli kisit (bilinen urun bosluğu):</b> Fatura hesabi acilmis bir musteri
 * silinemez (FR-007 ACC-003 → 409) ve fatura hesabini pasiflestiren bir API ucu de
 * yoktur (FR-011 ACC-005). Dolayisiyla bazi kayitlar veritabaninda kalir.
 *
 * <p>Bu durum <b>sessizce yutulmaz</b>: silinemeyen her kayit "artik" olarak loglanir
 * ve Allure raporuna eklenir. Artiklarin hangi kosumdan kaldigi {@link TestRunId}
 * eki sayesinde veritabaninda da izlenebilir.
 */
public final class TestDataCleaner {

    private static final Logger log = LoggerFactory.getLogger(TestDataCleaner.class);

    private static final Queue<CreatedCustomer> TRACKED = new ConcurrentLinkedQueue<>();

    private TestDataCleaner() {
    }

    /** Olusturulan her musteri {@link CustomerApi#onboard} icinde otomatik izlenir. */
    public static void track(CreatedCustomer customer) {
        TRACKED.add(customer);
    }

    public static int trackedCount() {
        return TRACKED.size();
    }

    /** Temizligin acik olup olmadigi; {@code -Dcleanup=false} ile kapatilabilir. */
    public static boolean isEnabled() {
        return !"false".equalsIgnoreCase(System.getProperty("cleanup", "true"));
    }

    /**
     * Izlenen tum musterileri soft-delete eder ve sonucu raporlar.
     * Hicbir kosulda istisna firlatmaz — temizlik hatasi suite sonucunu bozmamalidir.
     */
    public static void cleanUp() {
        if (TRACKED.isEmpty()) {
            return;
        }

        if (!isEnabled()) {
            log.warn("Temizlik kapali (-Dcleanup=false). {} test musterisi ortamda birakildi | runId={}",
                    TRACKED.size(), TestRunId.value());
            return;
        }

        int total = TRACKED.size();
        int deleted = 0;
        List<String> residue = new ArrayList<>();

        CreatedCustomer customer;
        while ((customer = TRACKED.poll()) != null) {
            try {
                int status = CustomerApi.deleteReturningStatus(customer.custId());
                if (status == 204 || status == 200) {
                    deleted++;
                } else {
                    residue.add(String.format("custId=%d natId=%s -> HTTP %d",
                            customer.custId(), customer.nationalId(), status));
                }
            } catch (RuntimeException e) {
                residue.add(String.format("custId=%d natId=%s -> %s",
                        customer.custId(), customer.nationalId(), e.getMessage()));
            }
        }

        report(total, deleted, residue);
    }

    private static void report(int total, int deleted, List<String> residue) {
        log.info("Test verisi temizligi | toplam={} | silinen={} | artik={} | runId={}",
                total, deleted, residue.size(), TestRunId.value());

        if (residue.isEmpty()) {
            return;
        }

        String detail = String.join(System.lineSeparator(), residue);

        // Artiklar bilinen bir urun kisitindan kaynaklanir; gorunur kalmalari gerekir.
        log.warn("SILINEMEYEN KAYITLAR ({} adet) — fatura hesabi olan musteriler silinemiyor "
                        + "(FR-007 ACC-003 / FR-011 ACC-005 bosluğu). runId={}{}{}",
                residue.size(), TestRunId.value(), System.lineSeparator(), detail);

        AllureAttachment.text("Temizlenemeyen test verisi (artik)", String.format(
                "runId=%s%ntoplam=%d, silinen=%d, artik=%d%n%n%s%n%n"
                        + "Sebep: fatura hesabi acilmis musteri silinemiyor ve hesabi pasiflestiren "
                        + "bir API ucu bulunmuyor (izlenebilirlik matrisi, bosluk #3).",
                TestRunId.value(), total, deleted, residue.size(), detail));
    }
}
