package org.learning.basic.shiro.web.filter;

import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.PassThruAuthenticationFilter;
import org.learning.basic.shiro.service.IShiroSubject;
import org.learning.basic.utils.JsonUtils;
import org.springframework.http.HttpStatus;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

public class ShiroPassThruAuthenticationFilter extends PassThruAuthenticationFilter {

    private IShiroSubject shiroSubject;

    @Override
    protected Subject getSubject(ServletRequest request, ServletResponse response) {
        return shiroSubject.subject((HttpServletRequest) request);
    }

    @Override
    protected void redirectToLogin(ServletRequest request, ServletResponse response) {
        JsonUtils.status(response, HttpStatus.UNAUTHORIZED, HttpStatus.UNAUTHORIZED.getReasonPhrase());
    }

    public void setShiroSubject(IShiroSubject shiroSubject) {
        this.shiroSubject = shiroSubject;
    }
}
