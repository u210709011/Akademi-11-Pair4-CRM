package com.crmlite.ui.pages.components;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Ust menu: dil secici, profil menusu ve Logout.
 *
 * <p><b>UYARI — FR-001 ACC-012/013 (Logout) UI'da uygulanmamistir.</b>
 * Hem {@code navbar .profile-option.logout} hem {@code sidebar .nav-item.logout}
 * butonlarinda {@code (click)} baglayicisi yok ve {@code AuthService} icinde
 * {@code logout()} metodu bulunmuyor (yalnizca cagrilmayan bir {@code clearSession()} var).
 * Locator'lar, ozellik gelistirildiginde hazir olsun diye simdiden tanimlandi;
 * {@link #logout()} bugun tiklar ama oturum sonlanmaz.
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

    /**
     * Logout butonuna tiklar.
     *
     * <p>Bugun bu tiklama <b>oturumu sonlandirmaz</b> — butonun click handler'i yok
     * (yukaridaki sinif notuna bakiniz). FR-001 ACC-012/013 testleri bu nedenle
     * gecmeyecektir; bu bir otomasyon hatasi degil, uygulamadaki eksikliktir.
     */
    public void logout() {
        openProfileMenu();
        click(LOGOUT_BUTTON);
    }

    public void toggleSidebar() {
        click(MENU_TOGGLE);
    }
}
