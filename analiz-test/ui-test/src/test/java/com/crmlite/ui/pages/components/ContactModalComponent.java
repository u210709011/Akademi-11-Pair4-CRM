package com.crmlite.ui.pages.components;

import com.crmlite.ui.core.waits.AppConditions;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * FR-006 — "Edit Contact Information" modal'i (musteri detayi > Contact Medium sekmesi).
 *
 * <p>Adres modal'i ile ayni kabuk yapisini paylasir ({@code .modal-backdrop .modal-card},
 * {@code .modal-actions .save-button}) ama alanlari farklidir; bu yuzden ayri bir bilesen.
 * Alan id'leri tek bir ekranda kullanildigi icin {@link AddressModalComponent}'teki gibi
 * bir {@code Variant} ayrimina gerek yoktur.
 *
 * <p><b>Telefon alanlarinda dikkat:</b> uygulama {@code beforeinput} olayini yakalayip
 * rakam disi karakterleri engelliyor. Yani "abc" yazmak alani bos birakir — bu davranis
 * ayri bir "yalnizca rakam" hata mesaji uretir ({@code create.digitsOnlyError}) ve format
 * hatasindan farklidir.
 */
public class ContactModalComponent extends BaseComponent {

    private static final By ROOT = By.cssSelector(".modal-backdrop .modal-card");

    private static final By TITLE = By.cssSelector(".modal-card .modal-title");
    private static final By CLOSE_BUTTON = By.cssSelector(".modal-card .modal-close");
    private static final By CANCEL_BUTTON = By.cssSelector(".modal-card .modal-actions .cancel-button");
    private static final By SAVE_BUTTON = By.cssSelector(".modal-card .modal-actions .save-button");
    private static final By SAVE_ERROR = By.cssSelector(".modal-card .address-save-error");

    /** Modal'daki alanlar; id'ler {@code detail-customer.component.html} ile birebir. */
    public enum Field {
        EMAIL("contact-email"),
        MOBILE_PHONE("contact-mobilePhone"),
        HOME_PHONE("contact-homePhone"),
        FAX("contact-fax");

        private final String id;

        Field(String id) {
            this.id = id;
        }

        public String id() {
            return id;
        }

        public By locator() {
            return By.id(id);
        }
    }

    public ContactModalComponent(WebDriver driver) {
        super(driver, ROOT);
    }

    @Override
    protected String pageName() {
        return "Iletisim Bilgileri Modal";
    }

    // --- Alan doldurma ---

    public ContactModalComponent enterEmail(String email) {
        type(Field.EMAIL.locator(), email);
        return this;
    }

    public ContactModalComponent enterMobilePhone(String phone) {
        type(Field.MOBILE_PHONE.locator(), phone);
        return this;
    }

    public ContactModalComponent enterHomePhone(String phone) {
        type(Field.HOME_PHONE.locator(), phone);
        return this;
    }

    public ContactModalComponent enterFax(String fax) {
        type(Field.FAX.locator(), fax);
        return this;
    }

    /** Alani fixture'daki {@code field} adina gore doldurur (veri gudumlu testler icin). */
    public ContactModalComponent enter(Field field, String value) {
        type(field.locator(), value);
        return this;
    }

    /**
     * Alandan odagi kaldirir.
     *
     * <p>Sablonda hata mesajlari {@code invalid() && touched()} sartina bagli
     * ({@code detail-customer.component.html}). Odak alanda kaldigi surece alan
     * "touched" sayilmaz ve <b>hata mesaji hic gorunmez</b> — yazdiktan sonra
     * dogrulama yapan her test once bu metodu cagirmalidir.
     */
    public ContactModalComponent blurField(Field field) {
        blur(field.locator());
        return this;
    }

    /** Yazip odagi kaldirir — validasyon dogrulamalarinin standart girisi. */
    public ContactModalComponent enterAndBlur(Field field, String value) {
        return enter(field, value).blurField(field);
    }

    /**
     * Zorunlu alanlari gecerli degerlerle doldurur.
     *
     * <p>Home Phone ve Fax dokumana gore zorunlu degildir; bu yuzden burada bilincli
     * olarak doldurulmaz.
     */
    public ContactModalComponent fillRequired(String email, String mobilePhone) {
        return enterEmail(email).enterMobilePhone(mobilePhone);
    }

    // --- Okuma (ACC-003: "mevcut bilgiler dolu gelir") ---

    public String value(Field field) {
        return getValue(field.locator());
    }

    public String emailValue() {
        return value(Field.EMAIL);
    }

    public String mobilePhoneValue() {
        return value(Field.MOBILE_PHONE);
    }

    public String homePhoneValue() {
        return value(Field.HOME_PHONE);
    }

    public String faxValue() {
        return value(Field.FAX);
    }

    public String title() {
        return getText(TITLE);
    }

    // --- Durum ---

    /** ACC-005: zorunlu alan eksik veya format gecersizken Save pasif olmalidir. */
    public boolean isSaveDisabled() {
        return remainsDisabled(SAVE_BUTTON);
    }

    public boolean isSaveEnabled() {
        return becomesEnabled(SAVE_BUTTON);
    }

    public boolean hasSaveError() {
        return isDisplayedAfterWait(SAVE_ERROR);
    }

    public String saveErrorText() {
        return getText(SAVE_ERROR);
    }

    public boolean hasFieldError(Field field) {
        return hasFieldError(field.id());
    }

    public String fieldError(Field field) {
        return fieldErrorText(field.id());
    }

    // --- Eylemler ---

    public void save() {
        click(SAVE_BUTTON);
    }

    public void cancel() {
        click(CANCEL_BUTTON);
    }

    /**
     * ACC-006: Cancel'a basar ve gosterilmesi <b>beklenen</b> onay diyalogunu dondurur.
     *
     * <p>Dokuman kaydedilmemis degisiklikler icin bir onay adimi sart kosar. Uygulamada
     * bu adim bulunmadigi icin donen diyalog acilmamis olabilir — cagiran test bunu
     * dogrular (bkz. {@code ContactCancelConfirmationTests}).
     */
    public ConfirmDialogComponent cancelExpectingConfirmation() {
        cancel();
        return new ConfirmDialogComponent(driver);
    }

    public void closeWithX() {
        click(CLOSE_BUTTON);
    }

    /** Modal DOM'dan tamamen kalkana kadar bekler (Angular {@code @if} blogu elementi siliyor). */
    public void waitUntilClosed() {
        wait.until(AppConditions.absentFromDom(ROOT));
    }

    /** Modal hala acik mi (ACC-006 — Cancel sonrasi beklenen davranisi dogrulamak icin). */
    public boolean isOpen() {
        return isDisplayed(ROOT);
    }
}
