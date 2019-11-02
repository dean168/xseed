package org.learning.basic.shiro.web.controller;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.learning.basic.core.SessionContext;
import org.learning.basic.dao.IHibernateOperations;
import org.learning.basic.i18n.utils.I18nUtils;
import org.learning.basic.shiro.domain.ShiroAccount;
import org.learning.basic.shiro.domain.ShiroStatus;
import org.learning.basic.shiro.service.IShiroAccountService;
import org.learning.basic.utils.StatusUtils.Status;
import org.learning.basic.web.controls.BasicController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;

import java.io.IOException;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.NONE;

public abstract class ShiroAccountController<A extends ShiroAccount> extends BasicController {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    @Qualifier(IHibernateOperations.SERVICE_ID)
    protected IHibernateOperations hibernateOperations;
    @Autowired
    @Qualifier(IShiroAccountService.SERVICE_ID)
    protected IShiroAccountService accountService;

    protected abstract Class<A> accountClass();

    /**
     * 创建用户
     *
     * @param account
     * @throws IOException
     */
//    @RequestMapping(method = POST, value = "/create", consumes = {APPLICATION_JSON_VALUE}, produces = {APPLICATION_JSON_VALUE})
    public Status<A> create(A account) throws IllegalAccessException, InstantiationException {
        if (StringUtils.isEmpty(account.getPassword())) {
            return new Status<>(false, I18nUtils.message("ACCOUNT.REGISTER.PASSWORD.NULL"));
        } else {
            account.setStatus(ShiroStatus.ACTIVE);
            return new Status<>(true, null, accountService.create(account));
        }
    }

    /**
     * 更新用户
     *
     * @param account
     * @throws IOException
     */
//    @RequestMapping(method = POST, value = "/update", consumes = {APPLICATION_JSON_VALUE}, produces = {APPLICATION_JSON_VALUE})
    public Status<A> update(A account) {
        return new Status<>(true, null, accountService.create(account));
    }

    protected Status<A> doLogin(LoginForm account) {
        UsernamePasswordToken token = new UsernamePasswordToken(account.getId(), account.getPassword(), account.isRme());
        try {
            // 登录操作
            SecurityUtils.getSubject().login(token);
            A userToUse = hibernateOperations.load(accountClass(), (String) SecurityUtils.getSubject().getPrincipal());
            if (userToUse.getStatus() != null && !ShiroStatus.ACTIVE.equals(userToUse.getStatus())) {
                if (logger.isDebugEnabled()) {
                    logger.debug("not active account#" + SecurityUtils.getSubject().getPrincipal() + " can not login");
                }
                SecurityUtils.getSubject().logout();
                return new Status<>(false, I18nUtils.message("ACCOUNT.LOGIN.USER.ERROR"));
            }
            if (logger.isDebugEnabled()) {
                logger.debug("login#" + userToUse.getId() + "(" + userToUse.getId() + ")");
            }
            // 初始化当前 session
            return session();
        } catch (AuthenticationException e) {
            if (logger.isDebugEnabled()) {
                logger.debug(e.getMessage());
            }
            return new Status<>(false, I18nUtils.message("ACCOUNT.LOGIN.USER.ERROR"));
        }
    }

    /**
     * 当前用户的 locale
     */
//    @RequestMapping(method = GET, value = "locale", produces = {APPLICATION_JSON_VALUE})
    public Status<String> locale() {
        return new Status<>(true, null, String.valueOf(I18nUtils.current()));
    }

    /**
     * 当前用户的 session
     */
//    @RequestMapping(method = GET, value = "session", produces = {APPLICATION_JSON_VALUE})
    public Status<A> session() {
        String accountId = (String) SecurityUtils.getSubject().getPrincipal();
        A account = null;
        if (StringUtils.isEmpty(accountId)) {
            if (logger.isDebugEnabled()) {
                logger.debug("Not logged in");
            }
            return new Status<>(HttpStatus.UNAUTHORIZED, I18nUtils.message("ACCOUNT.SESSION.FAIL"));
        } else if ((account = accountService.get(accountId)) == null) {
            if (logger.isDebugEnabled()) {
                logger.debug("Not exist account");
            }
            SecurityUtils.getSubject().logout();
            return new Status<>(HttpStatus.UNAUTHORIZED, I18nUtils.message("ACCOUNT.SESSION.FAIL"));
        } else {
            if (logger.isDebugEnabled()) {
                logger.debug("csession#" + account.getId() + "(" + account.getId() + ")");
            }
            // 返回状态
            return new Status<>(true, null, account);
        }
    }

    /**
     * 登录
     *
     * @param user
     */
//    @RequestMapping(method = POST, value = "/login", consumes = {APPLICATION_JSON_VALUE}, produces = {APPLICATION_JSON_VALUE})
    public Status<A> login(LoginForm user) {
        return doLogin(user);
    }

//    @RequestMapping(method = GET, value = "logout", produces = {APPLICATION_JSON_VALUE})
    public Status<?> logout() {
        try {
            SecurityUtils.getSubject().logout();
            SessionContext context = SessionContext.get();
            A account = context.account();
            if (account != null && logger.isDebugEnabled()) {
                logger.debug("User#" + account.getId() + " exit success");
            }
        } catch (Exception e) {
            logger.error(null, e);
        }
        return new Status<>(true, I18nUtils.message("ACCOUNT.LOGOUT.SUCCESS"));
    }

    public Status<?> delete(String id) {
        accountService.delete(id);
        return new Status<>(true);
    }

    @JsonAutoDetect(creatorVisibility = NONE, fieldVisibility = NONE, getterVisibility = NONE, setterVisibility = NONE, isGetterVisibility = NONE)
    public static class LoginForm {

        @JsonProperty("id")
        private String id;
        @JsonProperty("password")
        private String password;
        @JsonProperty("rme")
        private boolean rme;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public boolean isRme() {
            return rme;
        }

        public void setRme(boolean rme) {
            this.rme = rme;
        }
    }
}
