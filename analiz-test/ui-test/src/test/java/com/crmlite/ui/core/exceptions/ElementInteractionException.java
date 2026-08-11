package com.crmlite.ui.core.exceptions;

import org.openqa.selenium.By;

/**
 * Bir element ile etkilesim kurulamadiginda firlatilir.
 * Mesaj her zaman locator'i ve sayfa baglamini icerir; boylece hata ayiklama
 * icin ekran goruntusune bakmaya gerek kalmadan sorun anlasilir.
 */
public class ElementInteractionException extends FrameworkException {

    public ElementInteractionException(String action, By locator, String pageName, Throwable cause) {
        super(String.format("[%s] '%s' islemi basarisiz | locator=%s", pageName, action, locator), cause);
    }
}
