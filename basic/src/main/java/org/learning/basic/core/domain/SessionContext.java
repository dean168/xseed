package org.learning.basic.core.domain;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.learning.basic.core.IAccountService;
import org.learning.basic.utils.ServiceUtils;

public class SessionContext {

	public static final String ACCOUNT_ID = "accountId";
	public static final String ACCOUNT = "account";
	public static final String RESPONSE = "response";
	public static final String REQUEST = "request";
	public static final String LOCALE = "locale";
	private static final ThreadLocal<SessionContext> CTX = new ThreadLocal<SessionContext>();
	private static IAccountService AS = null;
	private Map<String, Object> attributes = new HashMap<String, Object>();

	public static SessionContext get() {
		return CTX.get();
	}

	public static void set(SessionContext context) {
		CTX.set(context);
	}

	private synchronized static IAccountService getUserService() {
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

	public HttpServletRequest currentRequest() {
		return (HttpServletRequest) current(REQUEST);
	}

	public void currentRequest(HttpServletRequest request) {
		current(REQUEST, request);
	}

	public HttpServletResponse currentResponse() {
		return (HttpServletResponse) current(RESPONSE);
	}

	public void currentResponse(HttpServletResponse response) {
		current(RESPONSE, response);
	}

	@SuppressWarnings("unchecked")
	public <U extends Account> U currentAccount() {
		U u = (U) current(ACCOUNT);
		if (u == null) {
			String id = (String) current(ACCOUNT_ID);
			if (id != null && (u = getUserService().getAccountById(id)) != null) {
				currentAccount(u);
			}
		}
		return u;
	}

	public <U extends Account> void currentAccount(U user) {
		current(ACCOUNT, user);
	}

	public String currentAccountId() {
		String userId = (String) current(ACCOUNT_ID);
		if (userId == null) {
			Account u = (Account) current(ACCOUNT);
			if (u != null) {
				current(ACCOUNT_ID, userId = u.getId());
			}
		}
		return userId;
	}

	public void currentAccountId(String accountId) {
		current(ACCOUNT_ID, accountId);
	}

	public String currentLocale() {
		return (String) current(LOCALE);
	}

	public void currentLocale(String locale) {
		current(LOCALE, locale);
	}
}
