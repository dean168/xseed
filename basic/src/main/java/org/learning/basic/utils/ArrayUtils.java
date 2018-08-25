package org.learning.basic.utils;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;

import org.learning.basic.core.domain.Ordered;

public abstract class ArrayUtils extends org.apache.commons.lang3.ArrayUtils {

	@SuppressWarnings("unchecked")
	public static <T> T[] add(T[] array, T object, Class<T> type) {
		if (array == null) {
			array = (T[]) Array.newInstance(type, 0);
		}
		T[] arrayToUse = (T[]) Array.newInstance(type, array.length + 1);
		System.arraycopy(array, 0, arrayToUse, 0, array.length);
		arrayToUse[array.length] = object;
		return arrayToUse;
	}

	@SuppressWarnings("unchecked")
	public static <T> T[] addAll(T[] array, T[] adds, Class<T> type) {
		if (adds == null) {
			return array;
		}
		if (array == null) {
			return adds;
		}
		T[] arrayToUse = (T[]) Array.newInstance(type, array.length + adds.length);
		System.arraycopy(array, 0, arrayToUse, 0, array.length);
		System.arraycopy(adds, 0, arrayToUse, array.length, adds.length);
		return arrayToUse;
	}

	@SuppressWarnings("unchecked")
	public static <T extends Ordered> T[] sort(Collection<T> collection, Class<T> type) {
		if (collection != null) {
			T[] array = (T[]) Array.newInstance(type, collection.size());
			int index = 0;
			for (T element : collection) {
				array[index++] = element;
			}
			return sort(array, type);
		} else {
			return (T[]) Array.newInstance(type, 0);
		}
	}

	@SuppressWarnings("unchecked")
	public static <T extends Ordered> T[] sort(T[] array, Class<T> clazz) {
		if (array != null) {
			sort(array);
		} else {
			array = (T[]) Array.newInstance(clazz, 0);
		}
		return array;
	}

	public static <T extends Ordered> T[] sort(T[] array) {
		Arrays.sort(array, (T o1, T o2) -> o1 == null || o1.getOrder() == null || o2 == null || o2.getOrder() == null ? 0 : o1.getOrder().compareTo(o2.getOrder()));
		return array;
	}
}