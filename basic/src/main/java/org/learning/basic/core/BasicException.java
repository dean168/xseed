package org.learning.basic.core;

import static org.apache.commons.lang3.ArrayUtils.EMPTY_OBJECT_ARRAY;
import static org.learning.basic.core.Errors.args;
import static org.learning.basic.core.Errors.cause;

public class BasicException extends RuntimeException {

    private static final long serialVersionUID = 0L;

    private String errcode;
    private Object[] errargs;

    public BasicException() {
    }

    public BasicException(String errcode) {
        this(errcode, EMPTY_OBJECT_ARRAY);
    }

    public BasicException(String errcode, Object... args) {
        super(errcode, cause(args));
        this.errcode = errcode;
        this.errargs = args(args);
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
