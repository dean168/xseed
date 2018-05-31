package org.learning.basic.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;

public abstract class ServiceUtils {

    private static ApplicationContext AC;

    public static boolean has(String id) {
        return AC.containsBean(id);
    }

    public static boolean has(String id, Class<?> clazz) {
        return AC.isTypeMatch(id, clazz);
    }

    public static Object get(String id) {
        return AC.getBean(id);
    }

    public static <T> T get(Class<T> clazz) {
        return AC.getBean(clazz);
    }

    public static <T> T get(String id, Class<T> clazz) {
        return AC.getBean(id, clazz);
    }

    public static <T> Map<String, T> list(Class<T> clazz) {
        return AC.getBeansOfType(clazz);
    }

    public static ApplicationContext getApplicationContext() {
        return AC;
    }

    public static final class Resolver implements ApplicationContextAware {

        @Override
        public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
            AC = applicationContext;
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
