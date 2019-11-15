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
import org.learning.basic.shiro.service.IShiroRealmService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Collection;

import static org.apache.shiro.util.ByteSource.*;

@Service(IShiroRealmService.SERVICE_ID)
public class ShiroRealmServiceImpl extends AuthorizingRealm implements IShiroRealmService {

    @Autowired
    @Qualifier(IShiroAccountService.SERVICE_ID)
    private IShiroAccountService accountService;

    public ShiroRealmServiceImpl() {
        setName(SERVICE_ID); //This name must match the name in the User class's getPrincipals() method
        HashedCredentialsMatcher matcher = new ShiroHashedCredentialsMatcher("SHA-256");
        matcher.setStoredCredentialsHexEncoded(false);
        setCredentialsMatcher(matcher);
    }

    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authcToken) throws AuthenticationException {
        UsernamePasswordToken token = (UsernamePasswordToken) authcToken;
        ShiroAccount account = getAccount(token.getUsername());
        return account != null ? createAuthenticationInfo(account) : null;
    }

    protected AuthenticationInfo createAuthenticationInfo(ShiroAccount account) {
        return new SimpleAuthenticationInfo(account.getId(), account.getPassword(), Util.bytes(account.getId()), getName());
    }

    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        Collection accounts = principals.fromRealm(getName());
        if (accounts.isEmpty()) {
            return null;
        }
        String accountId = (String) accounts.iterator().next();
        ShiroAccount account = getAccount(accountId);
        if (account == null) {
            return null;
        }
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        for (ShiroRole role : account.getRoles()) {
            info.addRole(role.getId());
            info.addStringPermissions(role.permissions());
        }
        return info;
    }

    protected ShiroAccount getAccount(String id) {
        return accountService.get(id);
    }

    @Override
    public int getOrder() {
        return Integer.MAX_VALUE;
    }
}
