package org.learning.basic.dao;

import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import org.learning.basic.dao.support.SQLSupport.SQL;

public interface IJdbcOperations extends JdbcOperations {

	String BASIC_SERVICE_ID = "basic.jdbcOperations";

	<T> T query(SQL sql, int offset, int limit, ResultSetExtractor<T> rse);

	void query(SQL sql, int offset, int limit, RowCallbackHandler rch);

	<T> List<T> query(SQL sql, int offset, int limit, RowMapper<T> rowMapper);

	<T> T queryForObject(SQL sql, int offset, int limit, RowMapper<T> rowMapper);

	<T> T queryForObject(SQL sql, int offset, int limit, Class<T> requiredType);

	Map<String, Object> queryForMap(SQL sql, int offset, int limit);

	<T> List<T> queryForList(SQL sql, int offset, int limit, Class<T> elementType);

	List<Map<String, Object>> queryForList(SQL sql, int offset, int limit);

	SqlRowSet queryForRowSet(SQL sql, int offset, int limit);
}
