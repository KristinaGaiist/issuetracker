package com.axmor.errorhelper;

import com.axmor.exceptions.DataConnectionException;

public class ErrorHelper {
    private ErrorHelper() {
    }

    public static void trowDateBaseConnectionOrRequestException(Exception inner) throws DataConnectionException {
        throw new DataConnectionException("Error in sql request or connection.", inner);
    }

    public static void trowDateBaseConnectionOrRequestException() throws DataConnectionException {
        throw new DataConnectionException("Error in sql request or connection.");
    }

    public static void throwPropertiesFileNotFound(Exception inner) {
        throw new IllegalStateException("Properties file can't be found.", inner);
    }
}
