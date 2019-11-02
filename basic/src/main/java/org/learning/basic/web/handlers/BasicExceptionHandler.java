package org.learning.basic.web.handlers;

import com.fasterxml.jackson.core.JsonGenerator;
import org.learning.basic.core.BasicException;
import org.learning.basic.utils.JsonUtils.Jackson;
import org.learning.basic.utils.StatusUtils.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Locale;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

@RestControllerAdvice
public class BasicExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(BasicExceptionHandler.class);

    @Autowired
    private MessageSource messageSource;

    @ExceptionHandler({Throwable.class})
    public void onThrowable(Locale locale, Throwable e, HttpServletResponse response) throws IOException {
        response.setContentType(APPLICATION_JSON_UTF8_VALUE);
        try (OutputStream os = response.getOutputStream()) {
            try (JsonGenerator jg = Jackson.createGenerator(os)) {
                jg.writeStartObject();
                jg.writeNumberField("code", Status.FALSE);
                jg.writeStringField("message", messages(locale, e));
                jg.writeEndObject();
                jg.flush();
            }
        } finally {
            logger.error(null, e);
        }
    }

    protected String messages(Locale locale, Throwable e) {
        if (e instanceof BindException) {
            for (ObjectError error : ((BindException) e).getAllErrors()) {
                return messageSource.getMessage(error, locale);
            }
        }
        if (e instanceof MethodArgumentNotValidException) {
            for (ObjectError error : ((MethodArgumentNotValidException) e).getBindingResult().getAllErrors()) {
                return messageSource.getMessage(error, locale);
            }
        }
        if (e instanceof WebExchangeBindException) {
            for (ObjectError error : ((WebExchangeBindException) e).getBindingResult().getAllErrors()) {
                return messageSource.getMessage(error, locale);
            }
        }
        if (e instanceof BasicException) {
            BasicException error = (BasicException) e;
            return messageSource.getMessage(error.getErrcode(), error.getErrargs(), error.getMessage(), locale);
        }
        return "server error.";
    }
}
