package org.learning.basic.shiro.service;

import org.learning.basic.shiro.domain.ShiroRole;

public interface IShiroRoleService extends IShiroBasicService {

    String SERVICE_ID = "basic.shiroRoleService";

    <A extends ShiroRole> A create(A role);

    <A extends ShiroRole> A update(A role);

    void delete(String id);
}
