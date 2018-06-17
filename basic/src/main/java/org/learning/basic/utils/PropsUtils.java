package org.learning.basic.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.core.io.support.PropertiesLoaderSupport;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

import javax.annotation.PreDestroy;
import java.lang.reflect.Method;
import java.util.Map.Entry;
import java.util.Properties;

public class PropsUtils {

    private static final Logger logger = LoggerFactory.getLogger(PropsUtils.class);

    private static final Method MP = ReflectionUtils.findMethod(PropertiesLoaderSupport.class, "mergeProperties");

    private static ConfigurableListableBeanFactory current;

    private static Properties PROPS;

    public static final String WEB_APP_ROOT = "webAppRoot";

    public static void prepared() {
        if (PROPS == null) {
            Assert.notNull(current, "beanFactory must not be null");
            PROPS = new Properties();
            for (Entry<String, PropertiesLoaderSupport> entry : current.getBeansOfType(PropertiesLoaderSupport.class).entrySet()) {
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

    public static final class Resolver implements BeanFactoryPostProcessor {

        @Override
        public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
            if (logger.isInfoEnabled()) {
                logger.info("using beanFactory -> " + beanFactory.getClass().getName());
            }
            current = beanFactory;
            PROPS = null;
        }

        @PreDestroy
        public void destroy() {
            current = null;
            PROPS = null;
        }
    }
}
