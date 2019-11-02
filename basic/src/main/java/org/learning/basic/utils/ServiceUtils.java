package org.learning.basic.utils;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.util.Assert;

import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;

import static org.learning.basic.core.Asserts.Patterns.notNull;

public abstract class ServiceUtils {

    private static BeanFactory current;

    public static BeanFactory get() {
        notNull(current, "beanFactory must not be null");
        return current;
    }

    public static boolean has(String id) {
        return get().containsBean(id);
    }

    public static boolean has(String id, Class<?> clazz) {
        return get().isTypeMatch(id, clazz);
    }

    public static Object get(String id) {
        return get().getBean(id);
    }

    public static <T> T get(Class<T> clazz) {
        return get().getBean(clazz);
    }

    public static <T> T get(String id, Class<T> clazz) {
        return get().getBean(id, clazz);
    }

    public static <T> Map<String, T> list(Class<T> clazz) {
        return ((ListableBeanFactory) get()).getBeansOfType(clazz);
    }

    public static final class Resolver implements BeanFactoryPostProcessor, ApplicationContextAware {

        @Override
        public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
            if (current == null) {
                current = beanFactory;
            }
        }

        @Override
        public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
            current = applicationContext;
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
            synchronized (Listener.class) {
                while (QUEUES != null && !QUEUES.isEmpty()) {
                    executor.execute(QUEUES.remove(0));
                }
                QUEUES = null;
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
