package org.learning.basic.core;

import org.learning.basic.core.domain.Account;
import org.learning.basic.utils.ServiceUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;


public class SessionContext {

    public static final String ACCOUNT_ID = "accountId";
    public static final String ACCOUNT = "account";
    public static final String RESPONSE = "response";
    public static final String REQUEST = "request";
    public static final String LOCALE = "locale";

    private static final ThreadLocal<SessionContext> CTX = new ThreadLocal<>();
    private static IAccountService AS = null;
    private Map<String, Object> attributes = new HashMap<>();

    public static SessionContext get() {
        return CTX.get();
    }

    public static void set(SessionContext context) {
        CTX.set(context);
    }

    private synchronized static IAccountService getAccountService() {
        if (AS == null) {
            AS = ServiceUtils.get(IAccountService.SERVICE_ID, IAccountService.class);
        }
        return AS;
    }

    public SessionContext copy() {
        SessionContext context = new SessionContext();
        context.attributes.putAll(attributes);
        return context;
    }

    public Object current(String key) {
        return attributes.get(key);
    }

    public void current(String key, Object value) {
        attributes.put(key, value);
    }

    public HttpServletRequest request() {
        return (HttpServletRequest) current(REQUEST);
    }

    public void request(HttpServletRequest request) {
        current(REQUEST, request);
    }

    public HttpServletResponse response() {
        return (HttpServletResponse) current(RESPONSE);
    }

    public void response(HttpServletResponse response) {
        current(RESPONSE, response);
    }

    @SuppressWarnings("unchecked")
    public <U extends Account> U account() {
        U u = (U) current(ACCOUNT);
        if (u == null) {
            String id = (String) current(ACCOUNT_ID);
            if (id != null && (u = getAccountService().get(id)) != null) {
                account(u);
            }
        }
        return u;
    }

    public <U extends Account> void account(U user) {
        current(ACCOUNT, user);
    }

    public String accountId() {
        String userId = (String) current(ACCOUNT_ID);
        if (userId == null) {
            Account u = (Account) current(ACCOUNT);
            if (u != null) {
                current(ACCOUNT_ID, userId = u.getId());
            }
        }
        return userId;
    }

    public void accountId(String accountId) {
        current(ACCOUNT_ID, accountId);
    }

    public String locale() {
        return (String) current(LOCALE);
    }

    public void locale(String locale) {
        current(LOCALE, locale);
    }
}
