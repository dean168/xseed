package org.learning.basic.utils;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.hibernate.collection.internal.PersistentSet;
import org.learning.basic.core.domain.Basic;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

public abstract class BeanUtils extends org.springframework.beans.BeanUtils {

	private static final Field HS_FIELD = ReflectionUtils.findField(PersistentSet.class, "set");
	private static final ThreadLocal<ICTOperations> OPR = new ThreadLocal<ICTOperations>();
	private static final ICTOperations DO = new DefaultCTOperations();

	static {
		ReflectionUtils.makeAccessible(HS_FIELD);
	}

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

	public static <T extends Basic> T cachedTo(T src, T target, Type type) {
		return cachedTo(src, target, type, DO);
	}

	@SuppressWarnings("rawtypes")
	public static <T extends Basic> T cachedTo(T src, T target, Type type, ICTOperations operations) {
		Map cached = new HashMap<>();
		return copyTo(src, target, type, new DefaultCTOperations() {
            @SuppressWarnings("unchecked")
            @Override
            public <OT extends Basic> OT copyTo(OT src, OT target, Type type) {
                if (src == null) {
                    return target;
                }
                OT targetToUse = (OT) cached.get(src);
                if (targetToUse != null) {
                    return targetToUse;
                }
                targetToUse = operations.copyTo(src, target, type);
                if (src != null && targetToUse != null) {
                    cached.put(src, targetToUse);
                }
                return targetToUse;
            }
		});
	}

	public static <T extends Basic> T copyTo(T src, T target, Type type, ICTOperations opr) {
		// 只能在最外层调用
		Assert.isNull(OPR.get(), "thread local operations must be null");
		OPR.set(opr);
		try {
			return copyTo(src, target, type);
		} finally {
			OPR.set(null);
		}
	}

	@SuppressWarnings("unchecked")
	public static <T extends Basic> Set<T> copyTo(Set<T> src, Set<T> target, Type type) {
		if (src != null) {
			if (target == null) {
				Class<?> clazz = (Class<?>) ClassUtils.getUserClass(src);
				// hibernate 的集合特殊处理
				if (PersistentSet.class.isAssignableFrom(clazz)) {
					Set<T> set = (Set<T>) ReflectionUtils.getField(HS_FIELD, src);
					if (set != null) {
						clazz = (Class<?>) ClassUtils.getUserClass(set);
					} else {
						// 默认使用 HashSet
						clazz = HashSet.class;
					}
				}
				target = (Set<T>) instantiate(clazz);
			}
			for (T basic : src) {
				target.add(copyTo(basic, null, type));
			}
		}
		return target;
	}

    public static <T extends Basic> T copyTo(T src, T target, Type type) {
        if (src != null) {
            ICTOperations opr = OPR.get();
            return opr != null ? opr.copyTo(src, target, type) : DO.copyTo(src, target, type);
        } else {
            return target;
        }
    }

    @SuppressWarnings("unchecked")
    public static <T extends Basic> T ref(T src, IRefOperations opr) {
		if (src != null && StringUtils.isNotEmpty(src.getId())) {
	        Class<?> clazz = ClassUtils.getUserClass(src);
	        return (T) opr.load(clazz, src.getId());
		} else {
			return null;
		}
	}

    public interface IRefOperations {

        <T> T load(Class<T> entityClass, Serializable id);
    }

	public interface ICTOperations {

		<T extends Basic> T copyTo(T src, T target, Type type);
	}

	public static class DefaultCTOperations implements ICTOperations {

		@SuppressWarnings("unchecked")
		@Override
		public <T extends Basic> T copyTo(T src, T target, Type type) {
			if (src != null) {
				if (target == null) {
					Class<T> clazz = (Class<T>) ClassUtils.getUserClass(src);
					target = instantiate(clazz);
				}
				target = src.copyTo(target, type);
			}
			return target;
		}
	}

	public enum Type {

		/**
		 * 不拷贝 id，用于新增对象到数据库
		 */
		SAVE,
		/**
		 * 全量拷贝
		 */
		DEEP,
	}
}