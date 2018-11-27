package org.learning.basic.shiro.web.filter;

import org.apache.shiro.SecurityUtils;
import org.learning.basic.core.SessionContext;
import org.learning.basic.web.filter.SessionContextFilter;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

public class ShiroSessionContextFilter extends SessionContextFilter {

    @Override
    protected SessionContext current(ServletRequest request, ServletResponse response) {

        SessionContext context = super.current(request, response);

        context.accountId((String) SecurityUtils.getSubject().getPrincipal());

        return context;
    }

}
