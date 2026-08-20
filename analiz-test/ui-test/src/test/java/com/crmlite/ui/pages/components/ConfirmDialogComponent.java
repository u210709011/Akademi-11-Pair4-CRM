package com.crmlite.ui.pages.components;

import com.crmlite.ui.core.waits.AppConditions;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Silme onay diyalogu (FR-005 ACC-010 ve musteri silme).
 *
 * <p>Musteri silme ve adres silme diyaloglari <b>ayni CSS siniflarini</b> paylasir
 * ({@code .modal-card.delete-confirm-card}); ayri {@code @if} bloklarinda olduklari icin
 * ayni anda yalnizca biri acik olur. Hangisinin acik oldugu {@link #title()} ile ayirt edilir.
 */
public class ConfirmDialogComponent extends BaseComponent {

    private static final By ROOT = By.cssSelector(".modal-card.delete-confirm-card");

    private static final By TITLE = By.cssSelector(".delete-confirm-card .modal-title");
    private static final By MESSAGE = By.cssSelector(".delete-confirm-card .delete-confirm-message");
    private static final By ERROR = By.cssSelector(".delete-confirm-card .address-save-error");
    private static final By CONFIRM_BUTTON = By.cssSelector(".delete-confirm-card .app-button-danger");
    private static final By CANCEL_BUTTON = By.cssSelector(".delete-confirm-card .modal-actions .app-button-secondary");
    private static final By CLOSE_BUTTON = By.cssSelector(".delete-confirm-card .modal-close");

    public ConfirmDialogComponent(WebDriver driver) {
        super(driver, ROOT);
    }

    @Override
    protected String pageName() {
        return "Onay Diyalogu";
    }

    public boolean isOpen() {
        return isDisplayedAfterWait(ROOT);
    }

    public String title() {
        return getText(TITLE);
    }

    public String message() {
        return getText(MESSAGE);
    }

    /**
     * ACC-011: silme reddedildiginde diyalog icinde gosterilen hata
     * ("Please change the related billing address in customer account." vb.).
     */
    public boolean hasError() {
        return isDisplayedAfterWait(ERROR);
    }

    public String errorText() {
        return getText(ERROR);
    }

    public void confirm() {
        click(CONFIRM_BUTTON);
    }

    public void cancel() {
        click(CANCEL_BUTTON);
    }

    public void closeWithX() {
        click(CLOSE_BUTTON);
    }

    public void waitUntilClosed() {
        wait.until(AppConditions.absentFromDom(ROOT));
    }
}
