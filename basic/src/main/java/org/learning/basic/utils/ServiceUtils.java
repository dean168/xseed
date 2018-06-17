package org.learning.basic.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.util.Assert;

import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;

public abstract class ServiceUtils {

    private static final Logger logger = LoggerFactory.getLogger(ServiceUtils.class);

    private static ConfigurableListableBeanFactory current;

    public static void prepared() {
        Assert.notNull(current, "beanFactory must not be null");
    }

    public static boolean has(String id) {
        prepared();
        return current.containsBean(id);
    }

    public static boolean has(String id, Class<?> clazz) {
        prepared();
        return current.isTypeMatch(id, clazz);
    }

    public static Object get(String id) {
        prepared();
        return current.getBean(id);
    }

    public static <T> T get(Class<T> clazz) {
        prepared();
        return current.getBean(clazz);
    }

    public static <T> T get(String id, Class<T> clazz) {
        prepared();
        return current.getBean(id, clazz);
    }

    public static <T> Map<String, T> list(Class<T> clazz) {
        prepared();
        return current.getBeansOfType(clazz);
    }

    public static final class Resolver implements BeanFactoryPostProcessor {

        @Override
        public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
            if (logger.isInfoEnabled()) {
                logger.info("using beanFactory -> " + beanFactory.getClass().getName());
            }
            current = beanFactory;
        }

        @PreDestroy
        public void destroy() {
            current = null;
        }
    }

    public static final class Listener implements ApplicationListener<ContextRefreshedEvent> {

        private static List<Runnable> QUEUES = new ArrayList<>();
        private Executor executor;

        @Override
        public void onApplicationEvent(ContextRefreshedEvent event) {
            if (event.getApplicationContext().getParent() == null) {
                synchronized (Listener.class) {
                    while (QUEUES != null && !QUEUES.isEmpty()) {
                        executor.execute(QUEUES.remove(0));
                    }
                    QUEUES = null;
                }
            }
        }

        public void setExecutor(Executor executor) {
            this.executor = executor;
        }

        public static void prepared(Runnable runnable) {
            if (!queuing(runnable)) {
                runnable.run();
            }
        }

        private static boolean queuing(Runnable runnable) {
            synchronized (Listener.class) {
                if (QUEUES != null) {
                    QUEUES.add(runnable);
                    return true;
                }
            }
            return false;
        }
    }
}
