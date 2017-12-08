package org.learning.basic.utils;

import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.util.Assert;
import org.springframework.web.context.WebApplicationContext;

import org.learning.basic.core.domain.SessionContext;

public abstract class WebUtils extends org.springframework.web.util.WebUtils {

	private static String WEB_ROOT;

	public static String getWebRoot() {
        if (WEB_ROOT == null) {
            ApplicationContext ac = ServiceUtils.getApplicationContext();
            Assert.isTrue(ac instanceof WebApplicationContext, ac + " not instanceof " + WebApplicationContext.class.getName());
            WebApplicationContext wac = (WebApplicationContext) ac;
			WEB_ROOT = wac.getServletContext().getRealPath("");
		}
		return WEB_ROOT;
	}

	public static String remoteAddr() {
		return remoteAddr(SessionContext.get().currentRequest());
	}

	public static String remoteAddr(HttpServletRequest request) {
		if (request == null) {
			return null;
		}

		String ip = request.getHeader("x-forwarded-for");

		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}

		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}

		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}

		return ip.equals("0:0:0:0:0:0:0:1") ? "127.0.0.1" : ip;
	}

	public static String headers() {
		return headers(SessionContext.get().currentRequest());
	}

	public static String headers(HttpServletRequest request) {
		StringBuffer hs = new StringBuffer();
		Enumeration<String> en = request.getHeaderNames();
		while (en.hasMoreElements()) {
			String name = en.nextElement();
			String value = request.getHeader(name);
			hs.append(name).append("=").append(value).append(";");
		}
		return hs.toString();
	}

	public static String params() {
		return params(SessionContext.get().currentRequest());
	}

	public static String params(HttpServletRequest request) {
		StringBuffer ps = new StringBuffer();
		Enumeration<String> en = request.getParameterNames();
		while (en.hasMoreElements()) {
			String name = en.nextElement();
			String[] values = request.getParameterValues(name);
			if (!ArrayUtils.isEmpty(values)) {
				for (String value : values) {
					ps.append(name).append("=").append(value).append(";");
				}
			} else {
				ps.append(name).append("=").append(";");
			}
		}
		return ps.toString();
	}
	
	public static boolean hasParams(String... names) {
		return hasParams(SessionContext.get().currentRequest(), names);
	}

	public static boolean hasParams(HttpServletRequest request, String... names) {
		for (String name : names) {
			if (StringUtils.isEmpty(request.getParameter(name))) {
				return false;
			}
		}
		return true;
	}
}