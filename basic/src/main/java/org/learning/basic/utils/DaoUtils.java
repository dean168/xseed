package org.learning.basic.utils;

import org.springframework.util.Assert;

public abstract class DaoUtils {

	private static final String FROM = "FROM ";
	private static final String ORDER = "ORDER ";

	public static String count(String sql) {
		String uql = StringUtils.upperCase(sql);
		int index = uql.indexOf(FROM);
		String cql = count(sql, index);
		Assert.isTrue(uql.substring(0, index).indexOf(" distinct ") == -1, "The sql select field contains distinct, count all will be wrong: " + sql + " -> " + cql);
		return cql;
	}

	private static String count(String sql, int index) {
	    sql = sql.substring(index + FROM.length());
	    String uql = StringUtils.upperCase(sql);
		if ((index = uql.indexOf(ORDER)) != -1) {
		    sql = sql.substring(0, index);
		}
		return "select count(1) from " + sql;
	}
}
