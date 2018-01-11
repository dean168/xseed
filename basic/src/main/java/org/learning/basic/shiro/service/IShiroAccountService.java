package org.learning.basic.shiro.service;

import org.learning.basic.core.IAccountService;
import org.learning.basic.shiro.domain.ShiroAccount;
import org.learning.basic.shiro.domain.ShiroRole;

public interface IShiroAccountService extends IAccountService {

    String SERVICE_ID = "basic.shiroAccountService";

    String ACCOUNT_ROLE_CODE = "account";

    String ADMIN_ROLE_CODE = "admin";

    void register(ShiroAccount account, String... roles);

    ShiroAccount store(ShiroAccount account, String... roles);

    void delete(String id);

    ShiroRole getRoleByCode(String code);
}
