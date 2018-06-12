package com.axmor.helpers;

import com.axmor.ApplicationConstants;

public class StringHelper {
    private StringHelper() {
    }

    public static boolean isNullOrEmpty(String string) {
        return string == null || string.equals(ApplicationConstants.EMPTY);
    }
}
