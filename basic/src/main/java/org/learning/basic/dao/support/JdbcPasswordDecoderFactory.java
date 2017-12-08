package org.learning.basic.dao.support;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.util.Assert;

import org.learning.basic.dao.IJdbcPasswordDecoder;

public class JdbcPasswordDecoderFactory implements FactoryBean<String> {

	private IJdbcPasswordDecoder jdbcPasswordDecoder;
	private String password;

	@PostConstruct
	public void init() {
		Assert.notNull(jdbcPasswordDecoder, "jdbcPasswordDecoder must not be null");
	}

	@Override
	public String getObject() throws Exception {
		return jdbcPasswordDecoder.decode(password);
	}

	@Override
	public Class<?> getObjectType() {
		return String.class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

	public void setJdbcPasswordDecoder(IJdbcPasswordDecoder jdbcPasswordDecoder) {
		this.jdbcPasswordDecoder = jdbcPasswordDecoder;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
