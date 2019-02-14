package org.learning.basic.web.filters;

import org.learning.basic.utils.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AccessControlFilter implements Filter {

	private static final Logger logger = LoggerFactory.getLogger(AccessControlFilter.class);

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		HttpServletRequest requestToUse = (HttpServletRequest) request;
		HttpServletResponse responseToUse = (HttpServletResponse) response;

		responseToUse.addHeader("Access-Control-Allow-Origin", "*");
		responseToUse.addHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
		responseToUse.addHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept");

		if (logger.isDebugEnabled()) {
			logger.debug(WebUtils.remoteAddr(requestToUse) + " " + requestToUse.getMethod() + " " + requestToUse.getRequestURI());
		}

		chain.doFilter(request, response);
	}

	@Override
	public void destroy() {
	}
}
