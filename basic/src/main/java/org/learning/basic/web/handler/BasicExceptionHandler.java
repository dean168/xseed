package org.learning.basic.web.handler;

import org.learning.basic.utils.StatusUtils.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class BasicExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(BasicExceptionHandler.class);

    @ExceptionHandler({Throwable.class})
    @ResponseBody
    public Status onThrowable(Throwable e) {
        try {
            return new Status(Status.FALSE, "server error.");
        } finally {
            logger.error(null, e);
        }
    }
}
