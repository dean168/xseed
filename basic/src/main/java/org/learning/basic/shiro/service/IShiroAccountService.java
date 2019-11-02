package org.learning.basic.shiro.service;

import org.learning.basic.core.IAccountService;
import org.learning.basic.shiro.domain.ShiroAccount;

public interface IShiroAccountService extends IAccountService {

    String SERVICE_ID = "basic.shiroAccountService";

    <A extends ShiroAccount> A create(A account);

    <A extends ShiroAccount> A update(A account);

    void delete(String id);

}
