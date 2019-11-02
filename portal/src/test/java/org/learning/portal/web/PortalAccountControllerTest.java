package org.learning.portal.web;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.learning.basic.dao.IHibernateOperations;
import org.learning.basic.dao.support.SQLSupport.SQL;
import org.learning.basic.shiro.domain.AccountRole;
import org.learning.basic.shiro.service.IShiroRoleService;
import org.learning.basic.utils.StatusUtils.Status;
import org.learning.portal.domain.StaffAccount;
import org.learning.portal.domain.StaffRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:/META-INF/*/service/*.service.xml", "classpath*:/META-INF/*/servlet/*.servlet.xml"})
public class PortalAccountControllerTest {

    @Autowired
    @Qualifier(IHibernateOperations.SERVICE_ID)
    private IHibernateOperations hibernateOperations;
    @Autowired
    @Qualifier(IShiroRoleService.SERVICE_ID)
    private IShiroRoleService roleService;
    @Autowired
    private PortalAccountController accountController;

    @Test
    public void test1() throws InstantiationException, IllegalAccessException {
        long now = System.currentTimeMillis();
        StaffAccount account = new StaffAccount();
        account.setId("id" + now);
        account.setName("name" + now);
        account.setPassword("password" + now);
        Status<StaffAccount> status1 = accountController.create(account);
        assertEquals(200, status1.getCode());
        assertNotNull(status1.getData());
        assertNotNull(status1.getData().getId());
        assertEquals(account.getId(), status1.getData().getId());
        Status<?> status2 = accountController.delete(status1.getData().getId());
        assertEquals(200, status2.getCode());
    }

    @Test
    public void test2() throws InstantiationException, IllegalAccessException {
        long now = System.currentTimeMillis();
        // role
        StaffRole role = new StaffRole();
        role.setId("id" + now);
        role.setName("name" + now);
        role = roleService.create(role);
        assertNotNull(role.getId());
        // account
        StaffAccount account = new StaffAccount();
        account.setId("id" + now);
        account.setName("name" + now);
        account.setPassword("password" + now);
        account.getRoles().add(role);
        Status<StaffAccount> status333 = accountController.create(account);
        assertEquals(200, status333.getCode());
        assertNotNull(status333.getData());
        assertNotNull(status333.getData().getId());
        assertEquals(account.getId(), status333.getData().getId());
        // select relation
        SQL sql = new SQL();
        sql.append("select count(1) from ").append(AccountRole.class);
        sql.append(" where account.id = ?", account.getId());
        sql.append(" and role.id = ?", role.getId());
        assertEquals(1, hibernateOperations.count(sql.getSQL(), sql.getParams()));
        // delete relation
        sql = new SQL();
        sql.append("delete from ").append(AccountRole.class);
        sql.append(" where account.id = ?", account.getId());
        sql.append(" and role.id = ?", role.getId());
        hibernateOperations.xbulkUpdate(sql.getSQL(), sql.getParams());
        // load account no role
        hibernateOperations.xtx(() -> {
            StaffAccount accountToUse = hibernateOperations.get(StaffAccount.class, account.getId());
            assertNotNull(accountToUse);
            assertEquals(true, accountToUse.getRoles().isEmpty());
        });
        // delete
        Status<?> status2 = accountController.delete(account.getId());
        assertEquals(200, status2.getCode());
    }
}
