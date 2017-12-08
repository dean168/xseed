package org.learning.basic.shiro.service.support;

import org.apache.shiro.authc.UsernamePasswordToken;

public class UsernameHashSaltedPasswordToken extends UsernamePasswordToken {

    private static final long serialVersionUID = 3516606994756559526L;

    public UsernameHashSaltedPasswordToken(String username, String password, boolean rememberMe) {
        super(username, password, rememberMe);
    }
}
