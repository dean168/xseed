package org.learning.basic.utils;

import org.learning.basic.core.domain.Basic;
import org.springframework.util.ClassUtils;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;

public abstract class BeanUtils extends org.springframework.beans.BeanUtils {

    public static <B extends Basic> void reset(Collection<B> basics) {
        if (basics != null) {
            basics.stream().forEach(basic -> reset(basic));
        }
    }

    public static <B extends Basic> void reset(B basic) {
        if (basic != null) {
            basic.reset();
        }
    }

    public static <B extends Basic> void created(B basic, String createdBy, Date createdAt) {
        if (basic != null) {
            basic.created(createdBy, createdAt);
        }
    }

    public static <B extends Basic> void updated(B basic, String updatedBy, Date updatedAt) {
        if (basic != null) {
            basic.updated(updatedBy, updatedAt);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T extends Basic> T ref(T src, IRefOperations opr) {
        if (src != null && StringUtils.isNotEmpty(src.getId())) {
            Class<T> clazz = (Class<T>) ClassUtils.getUserClass(src);
            return (T) opr.load(clazz, src.getId());
        } else {
            return null;
        }
    }

    public interface IRefOperations {

        Object load(Class<?> clazz, Serializable id);
    }
}