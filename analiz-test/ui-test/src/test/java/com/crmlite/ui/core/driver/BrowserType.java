package com.crmlite.ui.core.driver;

import com.crmlite.ui.core.exceptions.FrameworkException;

import java.util.Arrays;
import java.util.stream.Collectors;

public enum BrowserType {

    CHROME,
    FIREFOX,
    EDGE;

    public static BrowserType from(String value) {
        if (value == null || value.isBlank()) {
            return CHROME;
        }
        return Arrays.stream(values())
                .filter(b -> b.name().equalsIgnoreCase(value.trim()))
                .findFirst()
                .orElseThrow(() -> new FrameworkException(String.format(
                        "Desteklenmeyen tarayici: '%s'. Gecerli degerler: %s",
                        value,
                        Arrays.stream(values()).map(b -> b.name().toLowerCase()).collect(Collectors.joining(", ")))));
    }
}
