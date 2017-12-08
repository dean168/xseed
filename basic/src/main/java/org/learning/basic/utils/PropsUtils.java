package org.learning.basic.utils;

import java.lang.reflect.Method;
import java.util.Map.Entry;
import java.util.Properties;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.support.PropertiesLoaderSupport;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.context.WebApplicationContext;

public class PropsUtils {

	private static Method MP = ReflectionUtils.findMethod(PropertiesLoaderSupport.class, "mergeProperties");

	private static Properties PROPS = new Properties();

	public static final String WEB_APP_ROOT = "webAppRoot";

	public static String get(String key, String defaultValue) {
		return PROPS.getProperty(key, defaultValue);
	}

	public static boolean get(String key, boolean defaultValue) {
		try {
			return Boolean.valueOf(PROPS.getProperty(key));
		} catch (Exception e) {
			return defaultValue;
		}
	}

	public static String replace(String text, String prefix, String suffix) {
	    return StringUtils.replace(text, prefix, suffix, (String key, StringBuffer src, int prefixIndex, int suffixIndex) -> {
            String value = PROPS.getProperty(key);
            return StringUtils.isEmpty(value) ? prefix + key + suffix : value;
        });
	}

	public static final class Resolver implements ApplicationContextAware {
		@Override
		public void setApplicationContext(ApplicationContext ac) throws BeansException {
			PROPS.clear();
			for (Entry<String, PropertiesLoaderSupport> entry : ac.getBeansOfType(PropertiesLoaderSupport.class).entrySet()) {
				ReflectionUtils.makeAccessible(MP);
				Properties props = (Properties) ReflectionUtils.invokeMethod(MP, entry.getValue());
				PROPS.putAll(props);
			}
			if (ac instanceof WebApplicationContext) {
			    WebApplicationContext wac = (WebApplicationContext) ac;
			    PROPS.put(WEB_APP_ROOT, wac.getServletContext().getRealPath(""));
			}
		}
	}
}
