package org.learning.portal.web;

import org.apache.shiro.authc.UsernamePasswordToken;
import org.learning.basic.shiro.domain.ShiroAccount;
import org.learning.basic.shiro.domain.ShiroStatus;
import org.learning.basic.shiro.web.controls.ShiroAccountController;
import org.learning.basic.utils.BeanUtils;
import org.learning.basic.utils.StatusUtils.Status;
import org.learning.portal.domain.StaffAccount;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.*;

@RestController
@RequestMapping("account")
public class PortalAccountController extends ShiroAccountController<StaffAccount> {

    @RequestMapping(method = POST, value = "/login", consumes = {APPLICATION_JSON_VALUE}, produces = {APPLICATION_JSON_VALUE})
    public Status<StaffAccount> login(@RequestBody LoginForm form) {
        try {
            Status status = super.login(new UsernamePasswordToken(form.getId(), form.getPassword(), form.isRme()));
            if (status.getCode() == Status.TRUE) {
                StaffAccount account = (StaffAccount)status.getData();
                if(account.getFailureCount() > 0) {
                    StaffAccount accountToUse = new StaffAccount();
                    BeanUtils.copyProperties(account, accountToUse);
                    accountToUse.setFailureCount(0);
                    accountToUse.setPassword(null); // 不修改密码
                    this.update(accountToUse);
                }
            } else {
                ShiroAccount acc = this.getAccount(form.getId());
                if(acc != null) {
                    StaffAccount accountToUse = new StaffAccount();
                    BeanUtils.copyProperties(acc, accountToUse);
                    accountToUse.setFailureCount(acc.getFailureCount() + 1);
                    if(accountToUse.getFailureCount() >= this.maxFailureCount) {
                        accountToUse.setStatus(ShiroStatus.DISABLED);
                    }
                    accountToUse.setPassword(null); // 不修改密码
                    this.update(accountToUse);
                }
            }
            return status;
        } catch (Exception e) {
            e.printStackTrace();
            return new Status(false, "登录失败");
        }
    }

    @Override
    @RequestMapping(method = POST, value = "/create", consumes = {APPLICATION_JSON_VALUE}, produces = {APPLICATION_JSON_VALUE})
    public Status<StaffAccount> create(@RequestBody StaffAccount account) throws IllegalAccessException, InstantiationException {
        return super.create(account);
    }

    @Override
    @RequestMapping(method = POST, value = "/update", consumes = {APPLICATION_JSON_VALUE}, produces = {APPLICATION_JSON_VALUE})
    public Status<StaffAccount> update(@RequestBody StaffAccount account) {
        return super.update(account);
    }

    @Override
    @RequestMapping(method = GET, value = "locale", produces = {APPLICATION_JSON_VALUE})
    public Status<String> locale() {
        return super.locale();
    }

    @Override
    @RequestMapping(method = GET, value = "session", produces = {APPLICATION_JSON_VALUE})
    public Status<StaffAccount> session() {
        return super.session();
    }

    @Override
    @RequestMapping(method = GET, value = "logout", produces = {APPLICATION_JSON_VALUE})
    public Status<?> logout() {
        return super.logout();
    }

    @RequestMapping(method = DELETE, value = "delete/{id}", produces = {APPLICATION_JSON_VALUE})
    public Status<?> delete(@PathVariable String id) {
        return super.delete(id);
    }
}
