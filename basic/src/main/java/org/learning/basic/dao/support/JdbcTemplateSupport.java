package org.learning.basic.dao.support;

import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import org.learning.basic.dao.IJdbcOperations;
import org.learning.basic.dao.dialect.DialectSupport;
import org.learning.basic.dao.support.SQLSupport.SQL;

public class JdbcTemplateSupport extends JdbcTemplate implements IJdbcOperations {

	private DialectSupport dialectSupport;

	public JdbcTemplateSupport() {
		super();
	}

	public JdbcTemplateSupport(DataSource dataSource, boolean lazyInit) {
		super(dataSource, lazyInit);
	}

	public JdbcTemplateSupport(DataSource dataSource) {
		super(dataSource);
	}

	public JdbcTemplateSupport(DataSource dataSource, boolean lazyInit, DialectSupport dialect) {
		super(dataSource, lazyInit);
		this.dialectSupport = dialect;
	}

	public JdbcTemplateSupport(DataSource dataSource, DialectSupport dialect) {
		super(dataSource);
		this.dialectSupport = dialect;
	}

	@Override
	public <T> T query(SQL sql, int offset, int limit, ResultSetExtractor<T> rse) {
		SQL sqlToUse = dialectSupport.getLimitString(sql.getSQL(), sql.getParams(), offset, limit);
		return query(sqlToUse.getSQL(), sqlToUse.getParams(), rse);
	}

	@Override
	public void query(SQL sql, int offset, int limit, RowCallbackHandler rch) {
		SQL sqlToUse = dialectSupport.getLimitString(sql.getSQL(), sql.getParams(), offset, limit);
		query(sqlToUse.getSQL(), sqlToUse.getParams(), rch);
	}

	@Override
	public <T> List<T> query(SQL sql, int offset, int limit, RowMapper<T> rowMapper) {
		SQL sqlToUse = dialectSupport.getLimitString(sql.getSQL(), sql.getParams(), offset, limit);
		return query(sqlToUse.getSQL(), sqlToUse.getParams(), rowMapper);
	}

	@Override
	public <T> T queryForObject(SQL sql, int offset, int limit, RowMapper<T> rowMapper) {
		SQL sqlToUse = dialectSupport.getLimitString(sql.getSQL(), sql.getParams(), offset, limit);
		return queryForObject(sqlToUse.getSQL(), sqlToUse.getParams(), rowMapper);
	}

	@Override
	public <T> T queryForObject(SQL sql, int offset, int limit, Class<T> requiredType) {
		SQL sqlToUse = dialectSupport.getLimitString(sql.getSQL(), sql.getParams(), offset, limit);
		return queryForObject(sqlToUse.getSQL(), sqlToUse.getParams(), requiredType);
	}

	@Override
	public Map<String, Object> queryForMap(SQL sql, int offset, int limit) {
		SQL sqlToUse = dialectSupport.getLimitString(sql.getSQL(), sql.getParams(), offset, limit);
		return queryForMap(sqlToUse.getSQL(), sqlToUse.getParams());
	}

	@Override
	public <T> List<T> queryForList(SQL sql, int offset, int limit, Class<T> elementType) {
		SQL sqlToUse = dialectSupport.getLimitString(sql.getSQL(), sql.getParams(), offset, limit);
		return queryForList(sqlToUse.getSQL(), sqlToUse.getParams(), elementType);
	}

	@Override
	public List<Map<String, Object>> queryForList(SQL sql, int offset, int limit) {
		SQL sqlToUse = dialectSupport.getLimitString(sql.getSQL(), sql.getParams(), offset, limit);
		return queryForList(sqlToUse.getSQL(), sqlToUse.getParams());
	}

	@Override
	public SqlRowSet queryForRowSet(SQL sql, int offset, int limit) {
		SQL sqlToUse = dialectSupport.getLimitString(sql.getSQL(), sql.getParams(), offset, limit);
		return queryForRowSet(sqlToUse.getSQL(), sqlToUse.getParams());
	}

	public void setDialectSupport(DialectSupport dialectSupport) {
		this.dialectSupport = dialectSupport;
	}
}
