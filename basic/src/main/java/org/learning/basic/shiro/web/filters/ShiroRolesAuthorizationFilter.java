package org.learning.basic.shiro.web.filters;

import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authz.RolesAuthorizationFilter;
import org.learning.basic.shiro.service.IShiroSubject;
import org.learning.basic.utils.JsonUtils;
import org.springframework.http.HttpStatus;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class ShiroRolesAuthorizationFilter extends RolesAuthorizationFilter {

    private IShiroSubject shiroSubject;

    @Override
    protected Subject getSubject(ServletRequest request, ServletResponse response) {
        return shiroSubject.subject((HttpServletRequest) request);
    }

    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws IOException {
        Subject subject = getSubject(request, response);
        if (subject.getPrincipal() == null) {
            saveRequestAndRedirectToLogin(request, response);
        } else {
            JsonUtils.status(response, HttpStatus.FORBIDDEN, HttpStatus.FORBIDDEN.getReasonPhrase());
        }
        return false;
    }

    @Override
    protected void redirectToLogin(ServletRequest request, ServletResponse response) {
        JsonUtils.status(response, HttpStatus.UNAUTHORIZED, HttpStatus.UNAUTHORIZED.getReasonPhrase());
    }

    public void setShiroSubject(IShiroSubject shiroSubject) {
        this.shiroSubject = shiroSubject;
    }
}
