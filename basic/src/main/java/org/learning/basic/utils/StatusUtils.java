package org.learning.basic.utils;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonGenerator;
import org.learning.basic.core.BasicException;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

public abstract class StatusUtils {

    protected void _writeStatus(OutputStream os, boolean status, String message) {
        _writeStatus(os, status, message, null);
    }

    protected void _writeStatus(OutputStream os, boolean status, String message, Map<String, String> props) {
        try {
            JsonGenerator jg = _createGenerator(os);
            jg.writeStartObject();
            jg.writeNumberField("errcode", status ? Status.TRUE : Status.FALSE);
            jg.writeStringField("message", message);
            if (props != null) {
                for (Map.Entry<String, String> entry : props.entrySet()) {
                    jg.writeStringField(entry.getKey(), entry.getValue());
                }
            }
            jg.writeEndObject();
            jg.flush();
        } catch (IOException e) {
            throw new BasicException(null, null, e);
        }
    }

    protected abstract JsonGenerator _createGenerator(OutputStream os) throws IOException;

    @JsonAutoDetect(creatorVisibility = JsonAutoDetect.Visibility.NONE, fieldVisibility = JsonAutoDetect.Visibility.NONE, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE, isGetterVisibility = JsonAutoDetect.Visibility.NONE)
    public static class Status {

        public static final int FALSE = 0;
        public static final int TRUE= 1;

        @JsonProperty("errcode")
        private int errcode;
        @JsonProperty("message")
        private String message;
        @JsonProperty("data")
        private Object data;

        public Status() {
        }

        public Status(boolean success) {
            this(success ? TRUE : FALSE, null, null);
        }

        public Status(boolean success, String message) {
            this(success ? TRUE : FALSE, message, null);
        }

        public Status(boolean success, String message, Object data) {
            this(success ? TRUE : FALSE, message, data);
        }

        public Status(int errcode) {
            this(errcode, null, null);
        }

        public Status(int errcode, String message) {
            this(errcode, message, null);
        }

        public Status(int errcode, String message, Object data) {
            this.errcode = errcode;
            this.message = message;
            this.data = data;
        }

        public int getErrcode() {
            return errcode;
        }

        public void setErrcode(int errcode) {
            this.errcode = errcode;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public Object getData() {
            return data;
        }

        public void setData(Object data) {
            this.data = data;
        }
    }


    @JsonAutoDetect(creatorVisibility = JsonAutoDetect.Visibility.NONE, fieldVisibility = JsonAutoDetect.Visibility.NONE, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE, isGetterVisibility = JsonAutoDetect.Visibility.NONE)
    public static class MediaStatus extends Status {

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
