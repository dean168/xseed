package org.learning.basic.utils;

import org.apache.commons.lang.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public abstract class DateUtils {

    public static final String DEFAULT_DATE_TIME = "yyyy-MM-dd HH:mm:ss";

    public static final String DEFAULT_YYYYMMDD = "yyyy-MM-dd";

    /**
     * 默认时区通过 -Duser.timezone=GMT+08 启动参数设置
     * @param pattern
     * @param source
     * @return
     */
    public static Date parse(String pattern, String source) {
        return parse(pattern, source, TimeZone.getDefault());
    }

    public static Date parse(String pattern, String source, TimeZone zone) {
        if (StringUtils.isEmpty(source)) {
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        sdf.setTimeZone(zone);
        try {
            return sdf.parse(source);
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
