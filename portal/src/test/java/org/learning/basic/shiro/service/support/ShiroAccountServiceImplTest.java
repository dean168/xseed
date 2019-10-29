package org.learning.basic.shiro.service.support;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.learning.basic.shiro.domain.ShiroRole;
import org.learning.basic.shiro.service.IShiroAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertNotNull;
import static org.learning.basic.shiro.service.IShiroAccountService.ACCOUNT_ROLE_CODE;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:/META-INF/*/service/*.service.xml", "classpath*:/META-INF/*/servlet/*.servlet.xml"})
public class ShiroAccountServiceImplTest {

    @Autowired
    @Qualifier(IShiroAccountService.SERVICE_ID)
    private IShiroAccountService accountService;

    @Test
    public void test1() {
        ShiroRole role = accountService.getRoleByCode(ACCOUNT_ROLE_CODE);
        assertNotNull(role);
    }
}
