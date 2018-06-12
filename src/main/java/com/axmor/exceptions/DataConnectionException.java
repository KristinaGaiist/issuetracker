package com.axmor.exceptions;

public class DataConnectionException extends Exception {
    public DataConnectionException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public DataConnectionException(String s) {
        super(s);
    }
}
