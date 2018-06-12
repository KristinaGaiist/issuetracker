package com.axmor.helpers;

public class ArgumentHelper {
    private ArgumentHelper() {
    }

    public static void ensureNotNull(String name, Object value) {
        if (value == null) {
            String message = String.format("%s can't be null.", name);
            throw new IllegalArgumentException(message);
        }
    }

    public static void ensureNotNullOrEmpty(String name, String value) {
        if (StringHelper.isNullOrEmpty(value)) {
            String message = String.format("%s can't be null or empty.", name);
            throw new IllegalArgumentException(message);
        }
    }
}
