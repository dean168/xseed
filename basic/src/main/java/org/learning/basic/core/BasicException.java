package org.learning.basic.core;

public class BasicException extends RuntimeException {

    private static final long serialVersionUID = 3239595764164918907L;

    private String errcode;

    public BasicException() {
    }

    public BasicException(String message) {
        this(null, message, null);
    }

    public BasicException(String errcode, String message) {
        this(errcode, message, null);
    }

    public BasicException(String errcode, String message, Throwable cause) {
        super(message, cause);
        this.errcode = errcode;
    }

    public String getErrcode() {
        return errcode;
    }

    public void setErrcode(String errcode) {
        this.errcode = errcode;
    }
}
