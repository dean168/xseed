package org.learning.basic.web.filters;

import org.learning.basic.core.SessionContext;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class SessionContextFilter implements Filter {

    @Override
    public void init(FilterConfig config) {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        current(request, response);
        chain.doFilter(request, response);
    }

    protected SessionContext current(ServletRequest request, ServletResponse response) {
        SessionContext.set(null);
        SessionContext context = new SessionContext();
        context.request((HttpServletRequest) request);
        context.response((HttpServletResponse) response);
        context.locale(request.getLocale().getLanguage());
        SessionContext.set(context);
        return context;
    }

    @Override
    public void destroy() {
    }
}
