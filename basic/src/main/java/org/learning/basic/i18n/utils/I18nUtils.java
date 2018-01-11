package org.learning.basic.i18n.utils;

import org.learning.basic.core.domain.SessionContext;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.util.Assert;
import org.springframework.web.servlet.LocaleResolver;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;

public abstract class I18nUtils {

    private static final String NOT_NULL_SC = "current context must not be null";
    private static final String NOT_NULL_SC_REQ = "current context request must not be null";

    private static MessageSource MS;
    private static LocaleResolver LR;

    public static Locale current() {
        SessionContext context = SessionContext.get();
        Assert.notNull(context, NOT_NULL_SC);
        HttpServletRequest request = context.request();
        Assert.notNull(request, NOT_NULL_SC_REQ);
        return LR.resolveLocale(request);
    }

    public static String message(String code, Object... args) {
        return message(current(), code, args);
    }

    public static String message(Locale locale, String code, Object... args) {
        return MS.getMessage(code, args, locale);
    }

    public static final class Resolver implements MessageSourceAware, BeanFactoryAware {

        @Override
        public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
            LR = beanFactory.getBean("localeResolver", LocaleResolver.class);
        }

        @Override
        public void setMessageSource(MessageSource messageSource) {
            MS = messageSource;
        }

    }
}
