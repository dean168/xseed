package org.learning.basic.web.filters;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.servlet.support.JstlUtils;

public class JstlExposeLocalizationContextFilter implements Filter {

	private WebApplicationContext wac;

	@Override
	public void init(FilterConfig config) throws ServletException {
		wac = WebApplicationContextUtils.getWebApplicationContext(config.getServletContext());
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
			ServletException {

		JstlUtils.exposeLocalizationContext((HttpServletRequest) request, wac);

		chain.doFilter(request, response);
	}

	@Override
	public void destroy() {
	}
}
