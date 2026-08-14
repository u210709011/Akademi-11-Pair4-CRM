package com.crmlite.ui.pages.components;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Ust menu: dil secici, profil menusu ve Logout.
 *
 * <p>11.08.2026: FR-001 ACC-012/013 (Logout) uygulandi. {@code AuthService.logout()}
 * oturumu temizleyip {@code /logout} cagrisi yapiyor, navbar ve sidebar butonlarinda
 * {@code (click)} baglayicisi var. {@link #logout()} artik gercekten oturumu sonlandirir.
 */
public class NavbarComponent extends BaseComponent {

    private static final By ROOT = By.cssSelector("nav.navbar");

    private static final By APP_TITLE = By.cssSelector("nav.navbar .app-title");
    private static final By LANGUAGE_BUTTON = By.cssSelector("nav.navbar .language-btn");
    private static final By PROFILE_BUTTON = By.cssSelector("nav.navbar .profile");
    private static final By PROFILE_MENU = By.cssSelector("nav.navbar .profile-menu");
    private static final By LOGOUT_BUTTON = By.cssSelector("nav.navbar .profile-option.logout");
    private static final By MENU_TOGGLE = By.cssSelector("nav.navbar .menu-btn");

    public NavbarComponent(WebDriver driver) {
        super(driver, ROOT);
    }

    @Override
    protected String pageName() {
        return "Navbar";
    }

    public String appTitle() {
        return getText(APP_TITLE);
    }

    /** Dil butonunda gosterilen kod (ornegin "EN" / "TR"). */
    public String currentLanguageCode() {
        return getText(LANGUAGE_BUTTON);
    }

    // --- FR-018 Dil Destegi ---

    /** ACC-001: ust menude dil secenegi bulunmalidir. */
    public boolean hasLanguageOption() {
        return isDisplayed(LANGUAGE_BUTTON);
    }

    /** Buyuk harfe normalize edilmis dil kodu — karsilastirmalar bunun uzerinden yapilir. */
    public String currentLanguage() {
        return currentLanguageCode().trim().toUpperCase();
    }

    /**
     * Dili degistirir ve buton etiketinin degismesini bekler.
     *
     * <p>Uygulamada acilir menu YOKTUR: tek buton EN ve TR arasinda dogrudan gecis yapar
     * (bilesendeki metot adi {@code toggleLangMenu} olsa da davranis budur).
     */
    public NavbarComponent toggleLanguage() {
        String before = currentLanguage();
        click(LANGUAGE_BUTTON);
        wait.until(driver -> !currentLanguage().equals(before));
        return this;
    }

    public NavbarComponent openProfileMenu() {
        if (!isDisplayed(PROFILE_MENU)) {
            click(PROFILE_BUTTON);
        }
        return this;
    }

    public boolean isLogoutVisible() {
        openProfileMenu();
        return isDisplayedAfterWait(LOGOUT_BUTTON);
    }

    /** Profil menusunu acar ve Logout'a tiklar; oturum sonlanir. */
    public void logout() {
        openProfileMenu();
        click(LOGOUT_BUTTON);
    }

    public void toggleSidebar() {
        click(MENU_TOGGLE);
    }
}
