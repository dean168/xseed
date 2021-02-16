package org.learning.basic.dao.support;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

import static org.learning.basic.core.Errors.Patterns.handler;

public class SQLSupport {

    public static class SQL extends SQLSupport {

        public static final String LIKE = "%";
        public static final int HQL = 1;
        public static final int SQL = 2;

        private int type = HQL;
        private final StringBuffer text = new StringBuffer();
        private final List<Object> params = new ArrayList<>();
        private int offset;

        public SQL() {
        }

        public SQL(int type) {
            this.type = type;
        }

        public SQL append(Class<?> clazz, Object... args) {
            return append(clazz.getName(), args);
        }

        public SQL append(String content, Object... args) {
            if (type == HQL) {
                for (int i = 0; i < content.length(); i++) {
                    char c = content.charAt(i);
                    text.append(c);
                    if (c == '?') {
                        text.append(offset++);
                    }
                }
            } else if (type == SQL) {
                text.append(content);
            } else {
                return handler("undefined type: " + type);
            }
            for (int i = 0; args != null && i < args.length; i++) {
                params.add(args[i]);
            }
            return this;
        }

        public SQL addParams(String... args) {
            Collections.addAll(params, args);
            return this;
        }

        public SQL addParams(Collection<?> args) {
            params.addAll(args);
            return this;
        }

        public SQL addParams(Object... args) {
            Collections.addAll(params, args);
            return this;
        }

        public SQL addInParams(String... args) {
            for (int i = 0; i < ArrayUtils.getLength(args); i++) {
                append("?", args[i]);
                if (i + 1 < args.length) {
                    text.append(", ");
                }
            }
            return this;
        }

        public SQL addInParams(Collection<?> args) {
            for (Iterator<?> it = args.iterator(); it.hasNext(); ) {
                append("?", it.next());
                if (it.hasNext()) {
                    text.append(", ");
                }
            }
            return this;
        }

        public SQL addInParams(Object... args) {
            for (int i = 0; i < ArrayUtils.getLength(args); i++) {
                append("?", args[i]);
                if (i + 1 < args.length) {
                    text.append(", ");
                }
            }
            return this;
        }

        public SQL appendIfExist(String content, Object... args) {
            if (exist(args)) {
                append(content, args);
            }
            return this;
        }

        public SQL appendIfExistForLike(String content, Object... args) {
            if (exist(args)) {
                Object[] argsToUse = new Object[args.length];
                for (int i = 0; i < args.length; i++) {
                    argsToUse[i] = LIKE + args[i] + LIKE;
                }
                append(content, argsToUse);
            }
            return this;
        }

        public SQL appendIfExistForLikeRight(String content, Object... args) {
            if (exist(args)) {
                Object[] argsToUse = new Object[args.length];
                for (int i = 0; i < args.length; i++) {
                    argsToUse[i] = args[i] + LIKE;
                }
                append(content, argsToUse);
            }
            return this;
        }

        protected boolean exist(Object... args) {
            if (ArrayUtils.isEmpty(args)) {
                return false;
            }
            for (Object arg : args) {
                if (arg != null) {
                    if (arg instanceof String) {
                        if (StringUtils.isNotEmpty((String) arg)) {
                            return true;
                        }
                    } else {
                        return true;
                    }
                }
            }
            return false;
        }

        public void removeEnd(int length) {
            text.delete(text.length() - length, text.length());
        }

        public void removeEnd(String remove) {
            if (StringUtils.endsWith(text.toString(), remove)) {
                removeEnd(remove.length());
            }
        }

        public boolean endsWith(String suffix) {
            return StringUtils.endsWith(text.toString(), suffix);
        }

        public int indexOf(String text) {
            return this.text.indexOf(text);
        }

        public String getSQL() {
            return text.toString();
        }

        public Object[] getParams() {
            return params.toArray(ArrayUtils.EMPTY_OBJECT_ARRAY);
        }
    }
}
