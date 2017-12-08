package org.learning.basic.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang.StringUtils;

public abstract class DateUtils {

	public static final String DEFAULT_DATE_TIME = "yyyy-MM-dd HH:mm:ss";

	public static final String DEFAULT_YYYYMMDD = "yyyy-MM-dd";

	public static Date parse(String pattern, String source) {
		if (StringUtils.isEmpty(source)) {
			return null;
		}

		try {
			return new SimpleDateFormat(pattern).parse(source);
		} catch (ParseException e) {
			throw new RuntimeException("can not parse " + source + " pattern " + pattern, e);
		}
	}

	public static String format(String pattern, Date source) {
		if (source == null) {
			return null;
		}

		return new SimpleDateFormat(pattern).format(source);
	}
}
