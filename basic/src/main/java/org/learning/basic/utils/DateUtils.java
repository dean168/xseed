package org.learning.basic.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import static org.learning.basic.core.Errors.Patterns.handler;

public abstract class DateUtils {

    public static final String DEFAULT_DATE_TIME = "yyyy-MM-dd HH:mm:ss";

    public static final String DEFAULT_YYYYMMDD = "yyyy-MM-dd";

    /**
     * 默认时区通过 -Duser.timezone=GMT+08 启动参数设置
     *
     * @param source
     * @param pattern
     * @return
     */
    public static Date parse(String source, String pattern) {
        return parse(source, pattern, TimeZone.getDefault());
    }

    public static Date parse(String source, String pattern, TimeZone zone) {
        if (StringUtils.isEmpty(source)) {
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        sdf.setTimeZone(zone);
        try {
            return sdf.parse(source);
        } catch (ParseException e) {
            return handler("can not parse " + source + " pattern " + pattern, e);
        }
    }

    public static Date parse(String source, String pattern, TimeZone zone, Date defaultDate) {
        if (StringUtils.isEmpty(source)) {
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        sdf.setTimeZone(zone);
        try {
            return sdf.parse(source);
        } catch (ParseException e) {
            return defaultDate;
        }
    }

    public static String format(String pattern, Date source) {
        if (source == null) {
            return null;
        }
        return new SimpleDateFormat(pattern).format(source);
    }

    public static Date dateOfBirth(String text) {
        if (StringUtils.isNotEmpty(text)) {
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.DAY_OF_YEAR, 1);
            cal.set(Calendar.YEAR, cal.get(Calendar.YEAR) - StringUtils.ints(text));
            cal.set(Calendar.DAY_OF_MONTH, cal.get(Calendar.DAY_OF_MONTH) + 1);
            return cal.getTime();
        }
        return null;
    }

    public static boolean between(Date now, Date start, Date end) {
        if (now == null || start == null || end == null) {
            return false;
        }
        if (now.getTime() == start.getTime() || now.getTime() == end.getTime()) {
            return true;
        }
        return between(calendar(now), calendar(start), calendar(end));
    }

    public static boolean between(Calendar now, Calendar start, Calendar end) {
        if (now == null || start == null || end == null) {
            return false;
        }
        if (now.getTimeInMillis() == start.getTimeInMillis() || now.getTimeInMillis() == end.getTimeInMillis()) {
            return true;
        }
        return now.after(start) && now.before(end);
    }

    public static Calendar calendar(Date date) {
        return date != null ? calendar(date.getTime()) : null;
    }

    public static Calendar calendar(long timeInMillis) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timeInMillis);
        return cal;
    }
}
