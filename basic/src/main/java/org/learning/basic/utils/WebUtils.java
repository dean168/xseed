package org.learning.basic.utils;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.learning.basic.core.domain.SessionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.HierarchicalBeanFactory;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

public abstract class WebUtils extends org.springframework.web.util.WebUtils {

    private static final Logger logger = LoggerFactory.getLogger(WebUtils.class);

    private static String webAppRoot;

    public static String getWebRoot() {
        if (webAppRoot == null) {
            BeanFactory currentToUse = ServiceUtils.get();
            while (webAppRoot == null) {
                if (currentToUse instanceof WebApplicationContext) {
                    WebApplicationContext wac = (WebApplicationContext) currentToUse;
                    webAppRoot = wac.getServletContext().getRealPath("");
                } else if (currentToUse instanceof HierarchicalBeanFactory) {
                    currentToUse = ((HierarchicalBeanFactory) currentToUse).getParentBeanFactory();
                } else {
                    String temp = FileUtils.getTempDirectoryPath();
                    if (logger.isInfoEnabled()) {
                        logger.info("webAppRoot not found, using -> " + temp);
                    }
                    return temp;
                }
            }
            if (logger.isInfoEnabled()) {
                logger.info("using webAppRoot -> " + webAppRoot);
            }
        }
        return webAppRoot;
    }

    public static String remoteAddr() {
        return remoteAddr(SessionContext.get().request());
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
        return headers(SessionContext.get().request());
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
        return params(SessionContext.get().request());
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
        return hasParams(SessionContext.get().request(), names);
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