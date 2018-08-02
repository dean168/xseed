package org.learning.basic.utils;

import org.apache.commons.io.FileUtils;
import org.springframework.core.env.PropertyResolver;

import java.util.Collection;

public class PropsUtils {

    private static PropertyResolver[] RESOLVERS;

    public static final String WEB_APP_ROOT = "webAppRoot";
    public static final String WEB_TMP_ROOT = "basic.temp.root";

    public static String get(String key) {
        if (WEB_APP_ROOT.equals(key)) {
            return WebUtils.getWebRoot();
        }
        if (RESOLVERS == null) {
            Collection<PropertyResolver> resolvers = ServiceUtils.list(PropertyResolver.class).values();
            RESOLVERS = resolvers.toArray(new PropertyResolver[resolvers.size()]);
        }
        for (int i = 0; i < RESOLVERS.length; i++) {
            String value = RESOLVERS[i].getProperty(key);
            if (value != null) {
                return WEB_TMP_ROOT.equals(key) && StringUtils.EMPTY.equals(value) ? FileUtils.getTempDirectoryPath() : value;
            }
        }
        return null;
    }

    public static String get(String key, String defaultValue) {
        String val = get(key);
        return (val == null) ? defaultValue : val;
    }

    public static boolean get(String key, boolean defaultValue) {
        try {
            return Boolean.valueOf(get(key));
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public static String replace(String text, String prefix, String suffix) {
        return StringUtils.replace(text, prefix, suffix, (String key, StringBuffer src, int prefixIndex, int suffixIndex) -> {
            String value = get(key);
            return StringUtils.isEmpty(value) ? prefix + key + suffix : value;
        });
    }
}
