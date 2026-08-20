package com.crmlite.ui.pages;

import com.crmlite.ui.core.config.ConfigLoader;
import com.crmlite.ui.core.config.FrameworkConfig;
import com.crmlite.ui.core.exceptions.ElementInteractionException;
import com.crmlite.ui.core.exceptions.PageNotLoadedException;
import com.crmlite.ui.core.utils.JsUtil;
import com.crmlite.ui.core.waits.AppConditions;
import com.crmlite.ui.core.waits.WaitFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.List;

/**
 * Tum Page Object'lerin ortak atasi.
 *
 * <p>Tasarim kararlari:
 * <ul>
 *   <li><b>PageFactory ({@code @FindBy}) kullanilmaz.</b> Proxy'ler Angular DOM'u
 *       yeniden olusturdugunda {@code StaleElementReferenceException} uretiyor ve
 *       hata ayiklamayi zorlastiriyor. Bunun yerine {@link By} sabitleri ile
 *       cagri aninda cozumleme yapilir.</li>
 *   <li><b>Assertion icermez.</b> Sayfa "durum dondurur", karari test verir;
 *       boylece ayni sayfa hem pozitif hem negatif senaryoda kullanilabilir.</li>
 *   <li>Her eylem kendi beklemesini icerir; test kodunda {@code wait} cagrisi olmaz.</li>
 * </ul>
 */
public abstract class BasePage {

    protected static final int STALE_RETRY_LIMIT = 3;

    protected final Logger log = LoggerFactory.getLogger(getClass());

    protected final WebDriver driver;
    protected final WebDriverWait wait;
    protected final FrameworkConfig config;

    protected BasePage(WebDriver driver) {
        this.driver = driver;
        this.config = ConfigLoader.get();
        this.wait = WaitFactory.defaultWait(driver);
    }

    // --- Loadable Component ---

    /** Sayfanin yuklendigini kanitlayan locator (her sayfa kendi kriterini tanimlar). */
    protected abstract By pageReadyLocator();

    /** Raporlarda ve hata mesajlarinda kullanilan okunabilir sayfa adi. */
    protected abstract String pageName();

    /** Sayfa yuklendi mi (beklemez, anlik kontrol). */
    public boolean isAt() {
        return !driver.findElements(pageReadyLocator()).isEmpty();
    }

    /** Sayfa yuklenene kadar bekler; yuklenmezse mevcut URL'yi iceren acik bir hata firlatir. */
    public void waitUntilLoaded() {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(pageReadyLocator()));
        } catch (TimeoutException e) {
            throw new PageNotLoadedException(pageName(), driver.getCurrentUrl(), e);
        }
    }

    // --- Temel etkilesimler ---

    protected WebElement find(By locator) {
        try {
            return wait.until(ExpectedConditions.presenceOfElementLocated(locator));
        } catch (TimeoutException e) {
            throw new ElementInteractionException("element bulma", locator, pageName(), e);
        }
    }

    protected List<WebElement> findAll(By locator) {
        return driver.findElements(locator);
    }

    protected void click(By locator) {
        withStaleRetry("tiklama", locator, () -> {
            WebElement element = wait.until(ExpectedConditions.elementToBeClickable(locator));
            JsUtil.scrollIntoView(driver, element);
            element.click();
        });
    }

    /** Alani temizleyip yeni degeri yazar. Bos deger gonderilirse yalnizca temizler. */
    protected void type(By locator, String value) {
        withStaleRetry("yazma", locator, () -> {
            WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
            JsUtil.scrollIntoView(driver, element);
            clearField(element);
            if (value != null && !value.isEmpty()) {
                element.sendKeys(value);
            } else {
                // Alani BOSALTMA durumu: clear() DOM'u temizliyor ancak Angular Signal Forms
                // modeli guncellenmeyebiliyor. Sonuc yaniltici olur — alan ekranda bos gorunur
                // ama form hala gecerli sayilir ve Save/Next aktif kalir.
                // (Yazma yolunda sendKeys zaten tus basina input olayi uretir; oraya dokunulmaz.)
                notifyFrameworkOfChange(element);
            }
        });
    }

    protected void clear(By locator) {
        type(locator, "");
    }

    /**
     * Alandan odagi kaldirir (TAB gonderir).
     *
     * <p>Uygulamadaki bircok format kurali {@code (blur)} olayina bagli
     * (or. {@code onNatIdBlur}, {@code onGsmBlur}); odak tasinmadan hata mesaji
     * hic gorunmez. {@code clear()} veya baska bir alana yazmak odagi guvenilir
     * sekilde tasimadigi icin acikca TAB gonderilir.
     */
    protected void blur(By locator) {
        find(locator).sendKeys(Keys.TAB);
    }

    protected String getText(By locator) {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(locator)).getText().trim();
        } catch (TimeoutException e) {
            throw new ElementInteractionException("metin okuma", locator, pageName(), e);
        }
    }

    protected String getValue(By locator) {
        return find(locator).getDomProperty("value");
    }

    protected String getAttribute(By locator, String attribute) {
        return find(locator).getDomAttribute(attribute);
    }

    protected void selectByVisibleText(By locator, String text) {
        withStaleRetry("liste secimi", locator, () -> {
            WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
            new Select(element).selectByVisibleText(text);
            notifyFrameworkOfChange(element);
        });
    }

    protected void selectByValue(By locator, String value) {
        withStaleRetry("liste secimi", locator, () -> {
            WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
            new Select(element).selectByValue(value);
            notifyFrameworkOfChange(element);
        });
    }

    /**
     * {@code <select>} degisimini Angular'a duyurur.
     *
     * <p>Selenium'un {@code Select} sinifi DOM'daki {@code value} degerini dogru sekilde
     * ayarliyor, ancak Angular <b>Signal Forms</b> modelini guncellemek icin gereken
     * olaylari her zaman tetiklemiyor. Sonuc, hata ayiklamasi zor bir durum:
     * secimin DOM degeri dogru gorunurken alan hala "This field is required." diyor
     * ve buton aktiflesmiyor.
     *
     * <p>Bu yuzden secimden sonra {@code input} ve {@code change} olaylari acikca
     * gonderilir — gercek bir kullanici etkilesiminde tarayicinin urettigi olaylarin aynisi.
     */
    /**
     * Alani temizler; {@code clear()} ise yaramazsa klavyeye duser.
     *
     * <p><b>Neden:</b> {@code WebElement.clear()} yalnizca DOM'daki {@code value}'yu bosaltip
     * {@code change} uretir, {@code input} uretmez. Material {@code MatDatepickerInput}
     * gibi bilesenler modellerini {@code input} olayindan guncelledigi icin model eski
     * degerde kalir; Angular degisiklik algilamasi bir sonraki turda bicimlenmis eski degeri
     * DOM'a geri yazar. Sonuc, tanisi zor bir birlestirme olur — {@code sendKeys} yeni metni
     * eskisinin <b>sonuna</b> ekler:
     * <pre>  "15 / 06 / 1990" + "01/01/1900" -> "15 / 06 / 199001/01/1900"</pre>
     * Alan gecersizlesir ve Save/Next hic aktiflesmez.
     *
     * <p>Ctrl+A ardindan DELETE gercek {@code beforeinput}/{@code input} olaylari uretir;
     * bu, gercek kullanicinin yaptigi seydir ve her bilesende calisir. Telefon alanlarindaki
     * {@code blockNonDigitInput} engelleyicisi silme olaylarini engellemez ({@code event.data == null}).
     *
     * <p>Once {@code clear()} denenir: hali hazirda calisan alanlarin davranisi degismesin.
     */
    private void clearField(WebElement element) {
        element.clear();
        String remaining = element.getDomProperty("value");
        if (remaining != null && !remaining.isEmpty()) {
            log.debug("clear() etkisiz kaldi (kalan='{}'), klavye ile temizleniyor", remaining);
            element.sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.DELETE);
        }
    }

    private void notifyFrameworkOfChange(WebElement element) {
        JsUtil.execute(driver,
                "arguments[0].dispatchEvent(new Event('input', { bubbles: true }));"
                        + "arguments[0].dispatchEvent(new Event('change', { bubbles: true }));",
                element);
    }

    // --- Durum sorgulari (assertion degil, bilgi dondururler) ---

    public boolean isDisplayed(By locator) {
        List<WebElement> elements = driver.findElements(locator);
        return !elements.isEmpty() && isSafelyDisplayed(elements.get(0));
    }

    /** Kisa bekleme ile gorunurluk kontrolu — "mesaj gosterilmelidir" tipi ACC'ler icin. */
    public boolean isDisplayedAfterWait(By locator) {
        try {
            WaitFactory.shortWait(driver).until(ExpectedConditions.visibilityOfElementLocated(locator));
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }

    public boolean isEnabled(By locator) {
        return find(locator).isEnabled();
    }

    /** "Buton aktif olmalidir" ACC'leri icin: aktiflesene kadar bekler. */
    public boolean becomesEnabled(By locator) {
        try {
            wait.until(AppConditions.buttonBecomesEnabled(locator));
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }

    /**
     * "Buton aktif OLMAMALIDIR" ACC'leri icin: kisa bir sure boyunca pasif kaldigini dogrular.
     * Anlik kontrol, Angular henuz render etmemisken yanlis-gecer uretir.
     */
    public boolean remainsDisabled(By locator) {
        try {
            WaitFactory.observationWait(driver, Duration.ofSeconds(2))
                    .until(AppConditions.buttonBecameEnabled(locator));
            return false; // bir an bile aktiflesti
        } catch (TimeoutException e) {
            return true;  // sure boyunca pasif kaldi
        }
    }

    // --- Alan hatalari ---

    /**
     * Bir form alanina ait {@code .field-error} mesajinin locator'i.
     *
     * <p>Uygulamada hata mesaji, input ile ayni sarmalayici icinde kardes olarak duruyor
     * (sarmalayici sinifi ekrana gore degisiyor: {@code form-field} / {@code modal-field} /
     * {@code filter-field} / login ekraninda {@code form-group}). Bu yuzden alanin id'sinden
     * yukari cikip sarmalayici icinde aranir.
     */
    protected By fieldErrorFor(String fieldId) {
        return By.xpath(String.format(
                "//*[@id='%s']/ancestor::div["
                        + "contains(@class,'form-field') or contains(@class,'modal-field')"
                        + " or contains(@class,'filter-field') or contains(@class,'form-group')][1]"
                        + "//span[contains(@class,'field-error')]",
                fieldId));
    }

    /** Alanda gorunur bir hata mesaji var mi (kisa bekleme ile). */
    public boolean hasFieldError(String fieldId) {
        return isDisplayedAfterWait(fieldErrorFor(fieldId));
    }

    /** Alan hata mesajinin metni; hata yoksa {@code null}. */
    public String fieldErrorText(String fieldId) {
        List<WebElement> errors = driver.findElements(fieldErrorFor(fieldId));
        return errors.isEmpty() ? null : errors.get(0).getText().trim();
    }

    // --- Ortak yardimcilar ---

    /** FR-003 / FR-004: tekillik + KPS dogrulamasi bitene kadar bekler. */
    protected void waitForIdentityVerification() {
        WaitFactory.longWait(driver).until(AppConditions.identityVerificationFinished());
    }

    public String currentUrl() {
        return driver.getCurrentUrl();
    }

    /**
     * Angular {@code @for} bloklari DOM'u yeniden olusturdugu icin stale referans
     * beklenen bir durumdur; tek noktada sinirli sayida yeniden cozumleme yapilir.
     */
    private void withStaleRetry(String action, By locator, Runnable operation) {
        StaleElementReferenceException lastError = null;
        for (int attempt = 1; attempt <= STALE_RETRY_LIMIT; attempt++) {
            try {
                operation.run();
                return;
            } catch (StaleElementReferenceException e) {
                lastError = e;
                log.debug("Stale element, yeniden cozumleniyor ({}/{}) | {}", attempt, STALE_RETRY_LIMIT, locator);
            } catch (TimeoutException e) {
                throw new ElementInteractionException(action, locator, pageName(), e);
            }
        }
        throw new ElementInteractionException(action, locator, pageName(), lastError);
    }

    private boolean isSafelyDisplayed(WebElement element) {
        try {
            return element.isDisplayed();
        } catch (StaleElementReferenceException e) {
            return false;
        }
    }
}
