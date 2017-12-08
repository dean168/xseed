package org.learning.basic.shiro.service.support;

import org.apache.commons.lang.StringUtils;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;

public class ShiroHashedCredentialsMatcher extends HashedCredentialsMatcher {

    public ShiroHashedCredentialsMatcher(String hashAlgorithmName) {
        super(hashAlgorithmName);
    }

    @Override
    public boolean doCredentialsMatch(AuthenticationToken token, AuthenticationInfo info) {
        if (token instanceof UsernameHashSaltedPasswordToken) {
            UsernameHashSaltedPasswordToken tokenToUse = (UsernameHashSaltedPasswordToken) token;
            return StringUtils.equals(String.valueOf(info.getCredentials()), String.valueOf(tokenToUse.getPassword()));
        } else {
            return super.doCredentialsMatch(token, info);
        }
    }
}
