package org.learning.basic.shiro.web.filter;

import org.apache.shiro.web.filter.authc.PassThruAuthenticationFilter;
import org.learning.basic.utils.JsonUtils;
import org.springframework.http.HttpStatus;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;

public class ShiroPassThruAuthenticationFilter extends PassThruAuthenticationFilter {

    @Override
    protected void redirectToLogin(ServletRequest request, ServletResponse response) throws IOException {
        JsonUtils.status(response, HttpStatus.UNAUTHORIZED, HttpStatus.UNAUTHORIZED.getReasonPhrase());
    }
}
