package com.crmlite.ui.core.exceptions;

/** Beklenen sayfa verilen sure icinde yuklenmediginde firlatilir (Loadable Component). */
public class PageNotLoadedException extends FrameworkException {

    public PageNotLoadedException(String pageName, String currentUrl, Throwable cause) {
        super(String.format("'%s' sayfasi beklenen surede yuklenmedi | mevcut URL=%s", pageName, currentUrl), cause);
    }
}
