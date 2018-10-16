package org.learning.basic.web.handler;

import com.fasterxml.jackson.core.JsonGenerator;
import org.learning.basic.utils.JsonUtils.Jackson;
import org.learning.basic.utils.StatusUtils.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

@RestControllerAdvice
public class BasicExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(BasicExceptionHandler.class);

    @ExceptionHandler({Throwable.class})
    public void onThrowable(Throwable e, HttpServletResponse response) throws IOException {
        response.setContentType(APPLICATION_JSON_UTF8_VALUE);
        try (OutputStream os = response.getOutputStream()) {
            try (JsonGenerator jg = Jackson.createGenerator(os)) {
                jg.writeStartObject();
                jg.writeNumberField("errcode", Status.FALSE);
                jg.writeStringField("message", "server error.");
                jg.writeEndObject();
                jg.flush();
            }
        } finally {
            logger.error(null, e);
        }
    }
}
