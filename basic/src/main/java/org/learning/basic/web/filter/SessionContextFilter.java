package org.learning.basic.web.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.learning.basic.core.domain.SessionContext;

public class SessionContextFilter implements Filter {

	@Override
	public void init(FilterConfig config) throws ServletException {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
			ServletException {
		current(request, response);

		chain.doFilter(request, response);
	}

	protected SessionContext current(ServletRequest request, ServletResponse response) {
		SessionContext.set(null);

		SessionContext context = new SessionContext();
		context.currentRequest((HttpServletRequest) request);
		context.currentResponse((HttpServletResponse) response);
		context.currentLocale(request.getLocale().getLanguage());
		SessionContext.set(context);

		return context;
	}

	@Override
	public void destroy() {
	}
}
