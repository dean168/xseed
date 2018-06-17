package org.learning.basic.utils;

import org.springframework.core.io.support.PropertiesLoaderSupport;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.Map.Entry;
import java.util.Properties;

public class PropsUtils {

    private static final Method MP = ReflectionUtils.findMethod(PropertiesLoaderSupport.class, "mergeProperties");

    private static Properties PROPS;

    public static final String WEB_APP_ROOT = "webAppRoot";

    public static void prepared() {
        if (PROPS == null) {
            PROPS = new Properties();
            for (Entry<String, PropertiesLoaderSupport> entry : ServiceUtils.list(PropertiesLoaderSupport.class).entrySet()) {
                ReflectionUtils.makeAccessible(MP);
                Properties props = (Properties) ReflectionUtils.invokeMethod(MP, entry.getValue());
                PROPS.putAll(props);
            }
            PROPS.put(WEB_APP_ROOT, WebUtils.getWebRoot());
        }
    }

    public static String get(String key) {
        prepared();
        return PROPS.getProperty(key);
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
