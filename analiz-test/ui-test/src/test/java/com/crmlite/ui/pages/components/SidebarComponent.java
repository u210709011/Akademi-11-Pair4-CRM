package com.crmlite.ui.pages.components;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Sol menu: B2C / B2B / Approvals gecisleri ve Logout.
 *
 * <p>Navbar'daki gibi, buradaki Logout butonunun da {@code (click)} baglayicisi yoktur;
 * bkz. {@link NavbarComponent} sinif notu.
 */
public class SidebarComponent extends BaseComponent {

    private static final By ROOT = By.cssSelector("aside.sidebar");

    private static final By NAV_ITEMS = By.cssSelector("aside.sidebar .sidebar-nav .nav-item");
    private static final By ACTIVE_ITEM = By.cssSelector("aside.sidebar .sidebar-nav .nav-item.active .nav-label");
    private static final By LOGOUT_BUTTON = By.cssSelector("aside.sidebar .nav-item.logout");

    public SidebarComponent(WebDriver driver) {
        super(driver, ROOT);
    }

    @Override
    protected String pageName() {
        return "Sidebar";
    }

    public int navItemCount() {
        return findAll(NAV_ITEMS).size();
    }

    public String activeItemLabel() {
        return getText(ACTIVE_ITEM);
    }

    /** Menu etiketine gore gezinme (etiket i18n'e bagli oldugu icin dil sabitlenmis olmalidir). */
    public void selectByLabel(String label) {
        click(By.xpath(String.format(
                "//aside[contains(@class,'sidebar')]//button[contains(@class,'nav-item')]"
                        + "[.//span[contains(@class,'nav-label') and normalize-space()='%s']]",
                label)));
    }

    /** Bkz. {@link NavbarComponent#logout()} — bugun oturumu sonlandirmaz. */
    public void logout() {
        click(LOGOUT_BUTTON);
    }
}
