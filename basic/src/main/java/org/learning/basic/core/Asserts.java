package org.learning.basic.core;

import org.learning.basic.utils.ArrayUtils;
import org.springframework.util.StringUtils;

import static java.text.MessageFormat.format;

public abstract class Asserts extends org.springframework.util.Assert {

    public static final class Patterns {

        public static void isNull(Object object, String message, Object... args) {
            if (object != null) {
                throw new BasicException(ArrayUtils.isNotEmpty(args) ? format(message, args) : message);
            }
        }

        public static void notNull(Object object, String message, Object... args) {
            if (object == null) {
                throw new BasicException(ArrayUtils.isNotEmpty(args) ? format(message, args) : message);
            }
        }

        public static void hasText(String text, String message, Object... args) {
            if (!StringUtils.hasText(text)) {
                throw new BasicException(ArrayUtils.isNotEmpty(args) ? format(message, args) : message);
            }
        }

        public static void isTrue(boolean expression, String message, Object... args) {
            if (!expression) {
                throw new BasicException(ArrayUtils.isNotEmpty(args) ? format(message, args) : message);
            }
        }
    }

    public static final class Messages {

        public static void isNull(Object object, String message, Object... args) {
            if (object == null) {
                throw new BasicException(message, args);
            }
        }

        public static void notNull(Object object, String message, Object... args) {
            if (object == null) {
                throw new BasicException(message, args);
            }
        }

        public static void hasText(String text, String message, Object... args) {
            if (!StringUtils.hasText(text)) {
                throw new BasicException(message, args);
            }
        }

        public static void isTrue(boolean expression, String message, Object... args) {
            if (!expression) {
                throw new BasicException(message, args);
            }
        }
    }
}
