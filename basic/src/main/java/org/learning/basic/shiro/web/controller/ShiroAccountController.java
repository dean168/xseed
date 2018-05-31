package org.learning.basic.shiro.web.controller;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.learning.basic.core.domain.SessionContext;
import org.learning.basic.dao.IHibernateOperations;
import org.learning.basic.dao.support.SQLSupport.SQL;
import org.learning.basic.i18n.utils.I18nUtils;
import org.learning.basic.shiro.domain.ShiroAccount;
import org.learning.basic.shiro.domain.ShiroRole;
import org.learning.basic.shiro.domain.ShiroStatus;
import org.learning.basic.shiro.service.IShiroAccountService;
import org.learning.basic.utils.StatusUtils.Status;
import org.learning.basic.web.controller.BasicController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.util.Set;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.NONE;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

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
     * 用户注册
     * 同步前台页面请求，注册不会太并发，不会影响性能
     *
     * @param form
     * @throws IOException
     */
    @RequestMapping(method = POST, value = "/register",
            consumes = {APPLICATION_JSON_UTF8_VALUE},
            produces = {APPLICATION_JSON_UTF8_VALUE})
    public synchronized Status register(@RequestBody RegisterForm form) {
        if (StringUtils.isEmpty(form.getEmail())) {
            return new Status(false, I18nUtils.message("ACCOUNT.REGISTER.EMAIL.NULL"));
        } else if (StringUtils.isEmpty(form.getPassword())) {
            return new Status(false, I18nUtils.message("ACCOUNT.REGISTER.PASSWORD.NULL"));
        } else if (!StringUtils.equals(form.getPassword(), form.getConfirmPassword())) {
            return new Status(false, I18nUtils.message("ACCOUNT.REGISTER.CONFIRMPASSWORD.ERROR"));
        } else if (unique(null, form.getEmail())) {
            return new Status(false, I18nUtils.message("ACCOUNT.REGISTER.USER.EXIST"));
        } else {
            ShiroAccount accountToUse = BeanUtils.instantiate(accountClass());
            BeanUtils.copyProperties(form, accountToUse);
            accountToUse.setStatus(ShiroStatus.ACTIVE);
            accountService.register(accountToUse, IShiroAccountService.ACCOUNT_ROLE_CODE);
            return doLogin(form);
        }
    }

    protected boolean unique(String id, String email) {
        SQL sql = new SQL();
        sql.append("select count(*) from ").append(accountClass());
        sql.append(" where email = ?", email);
        if (StringUtils.isNotEmpty(id)) {
            sql.append(" and id <> ?", id);
        }
        return hibernateOperations.count(sql.getSQL(), sql.getParams()) > 0;
    }

    protected Status doLogin(LoginForm account) {
        UsernamePasswordToken token = new UsernamePasswordToken(account.getEmail(), account.getPassword(), account.isRme());
        try {
            // 登录操作
            SecurityUtils.getSubject().login(token);
            A userToUse = hibernateOperations.load(accountClass(), (String) SecurityUtils.getSubject().getPrincipal());
            if (userToUse.getStatus() != null && !ShiroStatus.ACTIVE.equals(userToUse.getStatus())) {
                if (logger.isDebugEnabled()) {
                    logger.debug("not active account#" + SecurityUtils.getSubject().getPrincipal() + " can not login");
                }
                SecurityUtils.getSubject().logout();
                return new Status(false, I18nUtils.message("ACCOUNT.LOGIN.USER.ERROR"));
            }
            if (logger.isDebugEnabled()) {
                logger.debug("login#" + userToUse.getId() + "(" + userToUse.getEmail() + ")");
            }
            // 初始化当前 session
            return session();
        } catch (AuthenticationException e) {
            if (logger.isDebugEnabled()) {
                logger.debug(e.getMessage());
            }
            return new Status(false, I18nUtils.message("ACCOUNT.LOGIN.USER.ERROR"));
        }
    }

    /**
     * 当前用户的session
     */
    @RequestMapping(method = GET, value = "locale",
            produces = {APPLICATION_JSON_UTF8_VALUE})
    public Status locale() {
        return new Status(true, null, String.valueOf(I18nUtils.current()));
    }

    /**
     * 当前用户的session
     */
    @RequestMapping(method = GET, value = "session",
            produces = {APPLICATION_JSON_UTF8_VALUE})
    public Status session() {
        String accountId = (String) SecurityUtils.getSubject().getPrincipal();
        A account = null;
        if (StringUtils.isEmpty(accountId)) {
            if (logger.isDebugEnabled()) {
                logger.debug("Not logged in");
            }
            return new Status(false, I18nUtils.message("ACCOUNT.SESSION.FAIL"));
        } else if ((account = accountService.getAccountById(accountId)) == null) {
            if (logger.isDebugEnabled()) {
                logger.debug("Not exist account");
            }
            SecurityUtils.getSubject().logout();
            return new Status(false, I18nUtils.message("ACCOUNT.SESSION.FAIL"));
        } else {
            if (logger.isDebugEnabled()) {
                logger.debug("csession#" + account.getId() + "(" + account.getEmail() + ")");
            }
            SessionStatus status = new SessionStatus(true, "current session");
            // 写当前用户信息
            BeanUtils.copyProperties(account, status);
            // 返回状态
            return status;
        }
    }

    /**
     * 登录
     *
     * @param user
     */
    @RequestMapping(method = POST, value = "/login",
            consumes = {APPLICATION_JSON_UTF8_VALUE},
            produces = {APPLICATION_JSON_UTF8_VALUE})
    public Status login(@RequestBody LoginForm user) {
        return doLogin(user);
    }

    /**
     * 更新当前用户密码
     */
    @RequestMapping(method = POST, value = "upasswd",
            consumes = {APPLICATION_JSON_UTF8_VALUE},
            produces = {APPLICATION_JSON_UTF8_VALUE})
    public Status upasswd(@RequestBody UPasswdForm form) {
        SessionContext context = SessionContext.get();
        A user = context.account();
        if (StringUtils.isEmpty(form.getOldpasswd())) {
            return new Status(false, I18nUtils.message("ACCOUNT.UPASSWD.OLDPASSWORD.NULL"));
        } else if (StringUtils.isEmpty(form.getNewpasswd())) {
            return new Status(false, I18nUtils.message("ACCOUNT.UPASSWD.NEWPASSWORD.NULL"));
        } else if (!StringUtils.equals(user.getPassword(), new Sha256Hash(form.getOldpasswd(), user.getEmail()).toBase64())) {
            return new Status(false, I18nUtils.message("ACCOUNT.UPASSWD.OLDPASSWORD.ERROR"));
        } else {
            user.setPassword(new Sha256Hash(form.getNewpasswd(), user.getEmail()).toBase64());
            hibernateOperations.xupdate(user);
            return new Status(true, I18nUtils.message("ACCOUNT.UPASSWD.SUCCESS"));
        }
    }

    @RequestMapping(method = GET, value = "logout",
            produces = {APPLICATION_JSON_UTF8_VALUE})
    public Status logout() throws IOException {
        try {
            SecurityUtils.getSubject().logout();
            SessionContext context = SessionContext.get();
            A account = context.account();
            if (account != null && logger.isDebugEnabled()) {
                logger.debug("User#" + account.getEmail() + " exit success");
            }
        } catch (Exception e) {
            logger.error(null, e);
        }
        return new Status(true, I18nUtils.message("ACCOUNT.LOGOUT.SUCCESS"));
    }

    @JsonAutoDetect(creatorVisibility = NONE, fieldVisibility = NONE, getterVisibility = NONE, setterVisibility = NONE, isGetterVisibility = NONE)
    public static class SessionStatus extends Status {

        @JsonProperty("id")
        private String id;
        @JsonProperty("name")
        private String name;
        @JsonProperty("email")
        private String email;
        @JsonProperty("avatar")
        private String avatar;
        @JsonProperty("post")
        private String post;
        @JsonProperty("roles")
        private Set<ShiroRole> roles;

        public SessionStatus(boolean success, String message) {
            super(success, message);
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }

        public String getPost() {
            return post;
        }

        public void setPost(String post) {
            this.post = post;
        }

        public Set<ShiroRole> getRoles() {
            return roles;
        }

        public void setRoles(Set<ShiroRole> roles) {
            this.roles = roles;
        }
    }

    @JsonAutoDetect(creatorVisibility = NONE, fieldVisibility = NONE, getterVisibility = NONE, setterVisibility = NONE, isGetterVisibility = NONE)
    public static class LoginForm {

        @JsonProperty("email")
        private String email;
        @JsonProperty("password")
        private String password;
        @JsonProperty("rme")
        private boolean rme;

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
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

    @JsonAutoDetect(creatorVisibility = NONE, fieldVisibility = NONE, getterVisibility = NONE, setterVisibility = NONE, isGetterVisibility = NONE)
    public static final class RegisterForm extends LoginForm {

        @JsonProperty("confirmPassword")
        private String confirmPassword;

        public String getConfirmPassword() {
            return confirmPassword;
        }

        public void setConfirmPassword(String confirmPassword) {
            this.confirmPassword = confirmPassword;
        }
    }

    @JsonAutoDetect(creatorVisibility = NONE, fieldVisibility = NONE, getterVisibility = NONE, setterVisibility = NONE, isGetterVisibility = NONE)
    public static final class UPasswdForm {

        @JsonProperty("oldpasswd")
        private String oldpasswd;
        @JsonProperty("newpasswd")
        private String newpasswd;

        public String getOldpasswd() {
            return oldpasswd;
        }

        public void setOldpasswd(String oldpasswd) {
            this.oldpasswd = oldpasswd;
        }

        public String getNewpasswd() {
            return newpasswd;
        }

        public void setNewpasswd(String newpasswd) {
            this.newpasswd = newpasswd;
        }
    }
}
