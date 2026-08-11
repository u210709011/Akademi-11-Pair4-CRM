package com.crmlite.ui.core.exceptions;

/**
 * Test on kosulu (API ile veri hazirlama) kurulamadiginda firlatilir.
 * Cagiran taraf bunu {@code SkipException}'a cevirmelidir: bu bir urun hatasi degil,
 * ortam/veri sorunudur ve gercek hata sayisini kirletmemelidir.
 */
public class TestDataSetupException extends FrameworkException {

    public TestDataSetupException(String message) {
        super(message);
    }

    public TestDataSetupException(String message, Throwable cause) {
        super(message, cause);
    }
}
