package org.learning.basic.utils;

public abstract class DaoUtils {

    private static final String SELECT = "SELECT ";
    private static final String FROM = "FROM ";
    private static final String DISTINCT = "DISTINCT ";
    private static final String ORDER = "ORDER ";

    public static String count(String sql) {
        String uql = StringUtils.upperCase(sql);
        int index = uql.indexOf(FROM);
        if (uql.indexOf(DISTINCT) > 0) {
            String cql = "select count(" + sql.substring(SELECT.length(), index) + ") " + sql.substring(index);
            if ((index = uql.indexOf(ORDER)) != -1) {
                cql = cql.substring(0, index);
            }
            return cql;
        } else {
            sql = sql.substring(index + FROM.length());
            if ((index = uql.indexOf(ORDER)) != -1) {
                sql = sql.substring(0, index);
            }
            return "select count(1) from " + sql;
        }
    }
}
