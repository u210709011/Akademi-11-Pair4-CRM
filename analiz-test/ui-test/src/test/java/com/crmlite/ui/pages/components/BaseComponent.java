package com.crmlite.ui.pages.components;

import com.crmlite.ui.pages.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Birden fazla ekranda tekrar eden UI parcalari icin ortak ata.
 *
 * <p>Klasik POM bu parcalari her sayfaya kopyalar. Adres karti, uc-nokta menu, modal,
 * sayfalama ve sonuc tablosu FR-002/003/005'te tekrar ettigi icin ayri bilesenlere
 * cikarildi; boylece ornegin adres modal'i tek sinifla yonetiliyor.
 *
 * <p>{@link BasePage}'i genisletir: bekleme, tiklama, yazma ve stale-retry davranisi
 * aynen gecerlidir.
 */
public abstract class BaseComponent extends BasePage {

    /** Bilesenin kok elementi; alt locator'lar bunun icinde aranir. */
    protected final By root;

    protected BaseComponent(WebDriver driver, By root) {
        super(driver);
        this.root = root;
    }

    @Override
    protected By pageReadyLocator() {
        return root;
    }

    /** Bilesen ekranda goruntuleniyor mu. */
    public boolean isVisible() {
        return isDisplayed(root);
    }
}
