package org.learning.basic.utils;

public abstract class DaoUtils {

    private static final String SELECT = "SELECT ";
    private static final String FROM = "FROM ";
    private static final String DISTINCT = "DISTINCT ";
    private static final String ORDER = "ORDER ";

    public static String count(String sql) {
        String uql = StringUtils.upperCase(sql);
        int di = uql.indexOf(DISTINCT), fi = uql.indexOf(FROM), oi = uql.indexOf(ORDER);
        if (di != -1) {
            if (oi != -1) {
                return "select count(" + sql.substring(SELECT.length(), fi) + ") " + sql.substring(fi, oi);
            } else {
                return "select count(" + sql.substring(SELECT.length(), fi) + ") " + sql.substring(fi);
            }
        } else {
            if (oi != -1) {
                return "select count(1) from " + sql.substring(fi + FROM.length(), oi);
            } else {
                return "select count(1) from " + sql.substring(fi + FROM.length());
            }
        }
    }
}
