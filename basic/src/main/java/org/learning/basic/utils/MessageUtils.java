package org.learning.basic.utils;

import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;

import org.learning.basic.core.domain.SessionContext;

public class MessageUtils implements MessageSourceAware {

	public static final String CURRENT_MESSAGE_KEY = "CURRENT_MESSAGE_KEY";

	private static MessageSource messageSource;

	public void setMessageSource(MessageSource messageSource) {
		MessageUtils.messageSource = messageSource;
	}

	public static String getMessage(String key, Object... args) {
		try {
			SessionContext context = SessionContext.get();

			return messageSource.getMessage(key, args, context.currentRequest().getLocale());
		} catch (Exception e) {
			return key;
		}
	}
}
