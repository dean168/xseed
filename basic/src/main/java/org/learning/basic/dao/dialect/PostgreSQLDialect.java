package org.learning.basic.dao.dialect;

import java.sql.Types;

import org.hibernate.dialect.PostgreSQL82Dialect;

public class PostgreSQLDialect extends PostgreSQL82Dialect {

	public PostgreSQLDialect() {

		super();

		registerColumnType( Types.BLOB, "bytea" );
	}
}
