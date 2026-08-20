package com.crmlite.ui.core.utils;

import com.crmlite.ui.core.exceptions.FrameworkException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/** Classpath'teki JSON fixture'larini okur ({@code src/test/resources/testdata/...}). */
public final class JsonReader {

    private static final ObjectMapper MAPPER = new ObjectMapper()
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

    private JsonReader() {
    }

    public static ObjectMapper mapper() {
        return MAPPER;
    }

    public static <T> List<T> readList(String classpathResource, Class<T> type) {
        try (InputStream in = open(classpathResource)) {
            return MAPPER.readValue(in, MAPPER.getTypeFactory().constructCollectionType(List.class, type));
        } catch (IOException e) {
            throw new FrameworkException("JSON okunamadi: " + classpathResource, e);
        }
    }

    public static <T> T read(String classpathResource, TypeReference<T> type) {
        try (InputStream in = open(classpathResource)) {
            return MAPPER.readValue(in, type);
        } catch (IOException e) {
            throw new FrameworkException("JSON okunamadi: " + classpathResource, e);
        }
    }

    public static String toJson(Object value) {
        try {
            return MAPPER.writeValueAsString(value);
        } catch (IOException e) {
            throw new FrameworkException("Nesne JSON'a cevrilemedi: " + value, e);
        }
    }

    private static InputStream open(String classpathResource) {
        InputStream in = JsonReader.class.getClassLoader().getResourceAsStream(classpathResource);
        if (in == null) {
            throw new FrameworkException("Classpath'te bulunamadi: " + classpathResource);
        }
        return in;
    }
}
