package org.learning.basic.shiro.service.support;

import org.learning.basic.dao.support.SQLSupport.SQL;
import org.learning.basic.shiro.domain.RolePermission;
import org.learning.basic.shiro.domain.ShiroPermission;
import org.learning.basic.shiro.service.IShiroPermissionService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ClassUtils;

@Service(IShiroPermissionService.SERVICE_ID)
public class ShiroPermissionServiceImpl extends ShiroBasicServiceImpl implements IShiroPermissionService {

    @Override
    @Transactional
    public <P extends ShiroPermission> P create(P permission) {
        return create(permission, permsToUse -> {
            cached.add(new ShiroEntry(ClassUtils.getUserClass(permsToUse), permsToUse.getId()));
            return permsToUse;
        });
    }

    @Override
    @Transactional
    public <P extends ShiroPermission> P update(P permission) {
        return update(permission, permsToUse -> {
            permsToUse = (P) hibernateOperations.load(ClassUtils.getUserClass(permission), permission.getId());
            BeanUtils.copyProperties(permission, permsToUse, "createdAt", "createdBy");
            cached.add(new ShiroEntry(ClassUtils.getUserClass(permsToUse), permsToUse.getId()));
            return permsToUse;
        });
    }

    @Override
    @Transactional
    public void delete(String id) {
        SQL sql = new SQL();
        sql.append("delete from ").append(RolePermission.class);
        sql.append(" where permission.id = ?", id);
        hibernateOperations.bulkUpdate(sql.getSQL(), sql.getParams());
        sql = new SQL();
        sql.append("delete from ").append(ShiroPermission.class);
        sql.append(" where id = ?", id);
        hibernateOperations.bulkUpdate(sql.getSQL(), sql.getParams());
        cached.add(new ShiroEntry(ShiroPermission.class, id));
    }
}
