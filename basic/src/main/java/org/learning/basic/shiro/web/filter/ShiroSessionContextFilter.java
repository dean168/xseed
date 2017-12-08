package org.learning.basic.shiro.web.filter;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.shiro.SecurityUtils;

import org.learning.basic.core.domain.SessionContext;
import org.learning.basic.web.filter.SessionContextFilter;

public class ShiroSessionContextFilter extends SessionContextFilter {

	@Override
	protected SessionContext current(ServletRequest request, ServletResponse response) {

		SessionContext context = super.current(request, response);

		context.currentAccountId((String) SecurityUtils.getSubject().getPrincipal());

		return context;
	}

}
