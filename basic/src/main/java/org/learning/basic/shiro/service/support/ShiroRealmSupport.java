package org.learning.basic.shiro.service.support;

import org.apache.shiro.authc.*;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.learning.basic.shiro.domain.ShiroAccount;
import org.learning.basic.shiro.domain.ShiroRole;
import org.learning.basic.shiro.service.IShiroAccountService;
import org.learning.basic.shiro.service.IShiroRealm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service(IShiroRealm.SERVICE_ID)
public class ShiroRealmSupport extends AuthorizingRealm implements IShiroRealm {

    @Autowired
    @Qualifier(IShiroAccountService.SERVICE_ID)
    private IShiroAccountService accountService;

    public ShiroRealmSupport() {
        setName(SERVICE_ID); //This name must match the name in the User class's getPrincipals() method
        HashedCredentialsMatcher matcher = new ShiroHashedCredentialsMatcher("SHA-256");
        matcher.setStoredCredentialsHexEncoded(false);
        setCredentialsMatcher(matcher);
    }

    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authcToken) throws AuthenticationException {
        UsernamePasswordToken token = (UsernamePasswordToken) authcToken;
        ShiroAccount user = accountService.getAccountByEmail(token.getUsername());
        if (user != null) {
            return new SimpleAuthenticationInfo(user.getId(), user.getPassword(),
                    ByteSource.Util.bytes(user.getEmail()), getName());
        } else {
            return null;
        }
    }

    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        String userId = (String) principals.fromRealm(getName()).iterator().next();
        ShiroAccount user = accountService.getAccountById(userId);
        if (user != null) {
            SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
            for (ShiroRole role : user.getRoles()) {
                info.addRole(role.getCode());
                info.addStringPermissions(role.getPermissionCodes());
            }
            return info;
        } else {
            return null;
        }
    }

}
