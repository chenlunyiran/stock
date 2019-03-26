package com.twotiger.stock.db.query.ex;

public class MorePageSizeException extends RuntimeException{
    public MorePageSizeException() {
    }

    public MorePageSizeException(Throwable cause) {
        super(cause);
    }

    public MorePageSizeException(String message) {
        super(message);
    }

    public MorePageSizeException(String message, Throwable cause) {
        super(message, cause);
    }

    public MorePageSizeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
