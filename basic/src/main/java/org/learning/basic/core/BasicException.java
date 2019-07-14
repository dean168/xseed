package org.learning.basic.core;

import static org.apache.commons.lang3.ArrayUtils.isNotEmpty;
import static org.apache.commons.lang3.ArrayUtils.remove;

public class BasicException extends RuntimeException {

    private static final long serialVersionUID = 3239595764164918907L;

    private String errcode;
    private Object[] errargs;

    public BasicException() {
    }

    public BasicException(String errcode) {
        this(errcode, null);
    }

    public BasicException(String errcode, Object... errargs) {
        super(errcode, cause(errargs));
        this.errcode = errcode;
        this.errargs = args(errargs);
    }

    static Throwable cause(Object... args) {
        return isNotEmpty(args) && args[0] instanceof Throwable ? (Throwable) args[0] : null;
    }

    static Object[] args(Object... args) {
        if (isNotEmpty(args) && args[0] instanceof Throwable) {
            return remove(args, 0);
        }
        return args;
    }

    public String getErrcode() {
        return errcode;
    }

    public void setErrcode(String errcode) {
        this.errcode = errcode;
    }

    public Object[] getErrargs() {
        return errargs;
    }

    public void setErrargs(Object[] errargs) {
        this.errargs = errargs;
    }
}
