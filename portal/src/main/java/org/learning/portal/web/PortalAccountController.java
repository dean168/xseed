package org.learning.portal.web;

import org.learning.basic.shiro.web.controller.ShiroAccountController;
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

    @Override
    protected Class<StaffAccount> accountClass() {
        return StaffAccount.class;
    }

    @Override
    @RequestMapping(method = POST, value = "/login", consumes = {APPLICATION_JSON_VALUE}, produces = {APPLICATION_JSON_VALUE})
    public Status<StaffAccount> login(@RequestBody LoginForm user) {
        return super.login(user);
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
