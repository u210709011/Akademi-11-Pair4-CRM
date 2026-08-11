package com.crmlite.ui.core.exceptions;

/** Cerceve kaynakli tum hatalarin ust sinifi. Test basarisizligi (AssertionError) ile karistirilmamalidir. */
public class FrameworkException extends RuntimeException {

    public FrameworkException(String message) {
        super(message);
    }

    public FrameworkException(String message, Throwable cause) {
        super(message, cause);
    }
}
