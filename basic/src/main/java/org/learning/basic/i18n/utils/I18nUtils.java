package org.learning.basic.i18n.utils;

import org.learning.basic.core.SessionContext;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.web.servlet.LocaleResolver;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;

import static org.learning.basic.core.Asserts.Patterns.notNull;

public abstract class I18nUtils {

    private static MessageSource MS;
    private static LocaleResolver LR;

    public static Locale current() {
        SessionContext context = SessionContext.get();
        notNull(context, "current context must not be null");
        HttpServletRequest request = context.request();
        notNull(request, "current context request must not be null");
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
