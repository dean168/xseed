package org.learning.basic.shiro.service;

import org.learning.basic.shiro.domain.ShiroPermission;

public interface IShiroPermissionService extends IShiroBasicService {

    String SERVICE_ID = "basic.shiroPermissionService";

    <P extends ShiroPermission> P create(P permission);

    <P extends ShiroPermission> P update(P permission);

    void delete(String id);
}
