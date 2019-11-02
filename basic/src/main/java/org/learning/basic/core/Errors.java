package org.learning.basic.core;

import static java.text.MessageFormat.format;
import static org.apache.commons.lang3.ArrayUtils.isNotEmpty;
import static org.apache.commons.lang3.ArrayUtils.remove;

public class Errors {

    public static final class Patterns {

        public static <T> T handler(String message, Object... args) {
            Throwable cause = cause(args);
            if (cause instanceof RuntimeException) {
                throw (RuntimeException) cause;
            }
            throw new BasicException(message != null ? format(message, args(args)) : null, cause);
        }
    }

    public static final class Messages {

        public static <T> T handler(String message, Object... args) {
            Throwable cause = cause(args);
            if (cause instanceof RuntimeException) {
                throw (RuntimeException) cause;
            }
            throw new BasicException(message, args);
        }
    }

    public static Throwable cause(Object... args) {
        return isNotEmpty(args) && args[0] instanceof Throwable ? (Throwable) args[0] : null;
    }

    public static Object[] args(Object... args) {
        if (isNotEmpty(args) && args[0] instanceof Throwable) {
            return remove(args, 0);
        }
        return args;
    }
}
