package org.learning.basic.utils;

import org.springframework.util.Assert;
import org.springframework.util.ConcurrentReferenceHashMap;

import java.lang.reflect.Field;
import java.util.Map;

public abstract class ReflectionUtils extends org.springframework.util.ReflectionUtils {

    private static final Field[] NO_FIELDS = new Field[0];
    private static final Map<Class<?>, Field[]> declaredFieldsCache = new ConcurrentReferenceHashMap<>(256);

    public static Field[] getDeclaredFields(Class<?> clazz) {
        Assert.notNull(clazz, "Class must not be null");
        Field[] result = declaredFieldsCache.get(clazz);
        if (result == null) {
            result = clazz.getDeclaredFields();
            declaredFieldsCache.put(clazz, result.length == 0 ? NO_FIELDS : result);
        }
        return result;
    }
}
