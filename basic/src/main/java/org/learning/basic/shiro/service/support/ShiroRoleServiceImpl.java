package org.learning.basic.shiro.service.support;

import org.learning.basic.dao.support.SQLSupport.SQL;
import org.learning.basic.shiro.domain.AccountRole;
import org.learning.basic.shiro.domain.RolePermission;
import org.learning.basic.shiro.domain.ShiroRole;
import org.learning.basic.shiro.service.IShiroRoleService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ClassUtils;

@Service(IShiroRoleService.SERVICE_ID)
public class ShiroRoleServiceImpl extends ShiroBasicServiceImpl implements IShiroRoleService {

    @Override
    @Transactional
    public <R extends ShiroRole> R create(R role) {
        return create(role, roleToUse -> roleToUse);
    }

    @Override
    @Transactional
    public <R extends ShiroRole> R update(R role) {
        return update(role, roleToUse -> {
            roleToUse = (R) hibernateOperations.load(ClassUtils.getUserClass(role), role.getId());
            BeanUtils.copyProperties(role, roleToUse, "createdAt", "createdBy");
            return roleToUse;
        });
    }

    @Override
    @Transactional
    public void delete(String id) {
        SQL sql = new SQL();
        sql.append("delete from ").append(RolePermission.class);
        sql.append(" where role.id = ?", id);
        hibernateOperations.bulkUpdate(sql.getSQL(), sql.getParams());
        sql = new SQL();
        sql.append("delete from ").append(AccountRole.class);
        sql.append(" where role.id = ?", id);
        hibernateOperations.bulkUpdate(sql.getSQL(), sql.getParams());
        sql = new SQL();
        sql.append("delete from ").append(ShiroRole.class);
        sql.append(" where id = ?", id);
        hibernateOperations.bulkUpdate(sql.getSQL(), sql.getParams());
    }
}
