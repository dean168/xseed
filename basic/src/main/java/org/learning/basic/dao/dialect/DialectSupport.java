package org.learning.basic.dao.dialect;

import org.apache.commons.lang.ArrayUtils;
import org.hibernate.MappingException;
import org.hibernate.dialect.Dialect;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

import org.learning.basic.dao.support.SQLSupport.SQL;
import org.learning.basic.utils.BeanUtils;

public class DialectSupport implements InitializingBean {
	private Dialect dialect;

	public DialectSupport() {
	}

	public DialectSupport(Dialect dialect) {
		this.init(dialect);
	}

	public void init(Dialect dialect) {
		this.dialect = dialect;
		afterPropertiesSet();
	}

	public void afterPropertiesSet() {
		Assert.notNull(dialect, "dialect is required.");
	}

	public SQL getLimitString(String query, int offset, int limit) {
		return getLimitString(query, null, offset, limit);
	}

	@SuppressWarnings("deprecation")
	public SQL getLimitString(String query, Object[] params, int offset, int limit) {
		SQL sql = new SQL();
		sql.append(dialect.getLimitString(query, dialect.supportsLimitOffset() ? offset : 0, dialect.useMaxForLimit() ? offset + limit : limit));
		// 获得分页的参数
		int[] limitParams = dialect.supportsVariableLimit() ? getLimitParams(offset, limit) : null;

		// 假如参数在前面
		if (limitParams != null && !ArrayUtils.isEmpty(limitParams) && dialect.bindLimitParametersFirst()) {
			for (int limitParam : limitParams) {
				sql.addParams(limitParam);
			}
		}
		// 原来的参数
		if (!ArrayUtils.isEmpty(params)) {
			sql.addParams(params);
		}
		// 假如参数在后
		if (limitParams != null && !ArrayUtils.isEmpty(limitParams) && !dialect.bindLimitParametersFirst()) {
			for (int limitParam : limitParams) {
				sql.addParams(limitParam);
			}
		}

		return sql;
	}

	@SuppressWarnings("deprecation")
	private int[] getLimitParams(int offset, int limit) {
		int limitToUse = offset + limit;
		if (offset > 0 && dialect.supportsLimitOffset()) {
			int[] limitParams = new int[] { offset, limitToUse };
			if (dialect.bindLimitParametersInReverseOrder()) {
				ArrayUtils.reverse(limitParams);
			}
			return limitParams;
		} else {
			return new int[] { limitToUse };
		}
	}

	public String getSequenceNextValString(String sequenceName) throws MappingException {
		return dialect.getSequenceNextValString(sequenceName);
	}

	public void setDialect(Dialect dialect) {
		this.dialect = dialect;
	}

	public void setDialectClass(String dialectClass) throws ClassNotFoundException {
		Object dialectToUse = BeanUtils.instantiateClass(ClassUtils.forName(dialectClass, ClassUtils.getDefaultClassLoader()));
		Assert.isTrue(dialectToUse instanceof Dialect, "Class [" + dialectClass + "] not instanceof " + Dialect.class.getName());
		this.dialect = (Dialect) dialectToUse;
	}
}
