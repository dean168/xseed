package org.learning.basic.utils;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonGenerator;
import org.learning.basic.core.BasicException;
import org.springframework.http.HttpStatus;

import javax.servlet.ServletResponse;
import java.io.IOException;
import java.io.OutputStream;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.NONE;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

public abstract class StatusUtils {

    protected void _status(ServletResponse response, boolean status, String message) {
        _status(response, status ? Status.TRUE : Status.FALSE, message);
    }

    protected void _status(ServletResponse response, HttpStatus status, String message) {
        _status(response, status.value(), message);
    }

    protected void _status(ServletResponse response, int status, String message) {
        response.setContentType(APPLICATION_JSON_UTF8_VALUE);
        try (OutputStream os = response.getOutputStream()) {
            _status(os, status, message);
        } catch (IOException e) {
            throw new BasicException(null, null, e);
        }
    }

    protected void _status(OutputStream os, boolean status, String message) {
        _status(os, status ? Status.TRUE : Status.FALSE, message);
    }

    protected void _status(OutputStream os, HttpStatus status, String message) {
        _status(os, status.value(), message);
    }

    protected void _status(OutputStream os, int status, String message) {
        try {
            JsonGenerator jg = _createGenerator(os);
            jg.writeStartObject();
            jg.writeNumberField("code", status);
            jg.writeStringField("message", message);
            jg.writeEndObject();
            jg.flush();
        } catch (IOException e) {
            throw new BasicException(null, null, e);
        }
    }

    protected abstract JsonGenerator _createGenerator(OutputStream os) throws IOException;

    @JsonAutoDetect(creatorVisibility = NONE, fieldVisibility = NONE, getterVisibility = NONE, setterVisibility = NONE, isGetterVisibility = NONE)
    public static class Status<D> {

        public static final int FALSE = HttpStatus.INTERNAL_SERVER_ERROR.value();
        public static final int TRUE = HttpStatus.OK.value();

        @JsonProperty("code")
        private int code;
        @JsonProperty("message")
        private String message;
        @JsonProperty("data")
        private D data;

        public Status() {
        }

        public Status(boolean success) {
            this(success ? TRUE : FALSE, null, null);
        }

        public Status(boolean success, String message) {
            this(success ? TRUE : FALSE, message, null);
        }

        public Status(boolean success, String message, D data) {
            this(success ? TRUE : FALSE, message, data);
        }

        public Status(HttpStatus status) {
            this(status, null, null);
        }

        public Status(HttpStatus status, String message) {
            this(status, message, null);
        }

        public Status(HttpStatus status, String message, D data) {
            this(status.value(), message, data);
        }

        public Status(int code) {
            this(code, null, null);
        }

        public Status(int code, String message) {
            this(code, message, null);
        }

        public Status(int code, String message, D data) {
            this.code = code;
            this.message = message;
            this.data = data;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public D getData() {
            return data;
        }

        public void setData(D data) {
            this.data = data;
        }
    }


    @JsonAutoDetect(creatorVisibility = NONE, fieldVisibility = NONE, getterVisibility = NONE, setterVisibility = NONE, isGetterVisibility = NONE)
    public static class MediaStatus extends Status<String> {

        @JsonProperty("contentType")
        private String contentType;
        @JsonProperty("contentLength")
        private long contentLength;
        @JsonProperty("name")
        private String name;
        @JsonProperty("filename")
        private String filename;
        @JsonProperty("temp")
        private String temp;

        public String getContentType() {
            return contentType;
        }

        public void setContentType(String contentType) {
            this.contentType = contentType;
        }

        public long getContentLength() {
            return contentLength;
        }

        public void setContentLength(long contentLength) {
            this.contentLength = contentLength;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getFilename() {
            return filename;
        }

        public void setFilename(String filename) {
            this.filename = filename;
        }

        public String getTemp() {
            return temp;
        }

        public void setTemp(String temp) {
            this.temp = temp;
        }
    }
}
