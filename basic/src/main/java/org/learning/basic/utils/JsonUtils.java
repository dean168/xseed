package org.learning.basic.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONReader;
import com.alibaba.fastjson.JSONWriter;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;
import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module.Feature;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.learning.basic.core.BasicException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;

import javax.annotation.PostConstruct;
import javax.servlet.ServletResponse;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;

public abstract class JsonUtils extends StatusUtils {

    private static final JsonUtils JU = new JsonUtils() {
        @Override
        protected JsonGenerator _createGenerator(OutputStream os) throws IOException {
            return Jackson.JF.createGenerator(os, JsonEncoding.UTF8);
        }
    };

    public static void status(ServletResponse response, boolean status, String message) {
        JU._status(response, status, message);
    }

    public static void status(ServletResponse response, HttpStatus status, String message) {
        JU._status(response, status, message);
    }

    public static void status(ServletResponse response, int status, String message) {
        JU._status(response, status, message);
    }

    public static void status(OutputStream os, boolean status, String message) {
        JU._status(os, status, message);
    }

    public static void status(OutputStream os, HttpStatus status, String message) {
        JU._status(os, status, message);
    }

    public static void status(OutputStream os, int status, String message) {
        JU._status(os, status, message);
    }

    public static final class Jackson {

        public static final JsonFactory JF = new JsonFactory();
        public static final ObjectMapper OM = new ObjectMapper();

        static {
            OM.setSerializationInclusion(Include.NON_NULL);
            OM.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
            OM.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            OM.registerModule(new Hibernate5Module().enable(Feature.FORCE_LAZY_LOADING));
        }

        public static JsonGenerator createGenerator(File content) {
            try {
                return JF.createGenerator(content, JsonEncoding.UTF8);
            } catch (IOException e) {
                throw new BasicException(null, null, e);
            }
        }

        public static JsonGenerator createGenerator(OutputStream os) {
            try {
                return JF.createGenerator(os, JsonEncoding.UTF8);
            } catch (IOException e) {
                throw new BasicException(null, null, e);
            }
        }

        public static JsonGenerator createGenerator(Writer writer) {
            try {
                return JF.createGenerator(writer);
            } catch (IOException e) {
                throw new BasicException(null, null, e);
            }
        }

        public static JsonParser createParser(InputStream in) {
            try {
                return JF.createParser(in);
            } catch (IOException e) {
                throw new BasicException(null, null, e);
            }
        }

        public static JsonParser createParser(String content) {
            try {
                return JF.createParser(content);
            } catch (IOException e) {
                throw new BasicException(null, null, e);
            }
        }

        public static void writeValue(OutputStream os, Object value) {
            try {
                OM.writeValue(os, value);
            } catch (IOException e) {
                throw new BasicException(null, null, e);
            }
        }

        public static <T> T readValue(String content, TypeReference<T> typeReference) {
            try {
                return OM.readValue(content, typeReference);
            } catch (IOException e) {
                throw new BasicException(null, null, e);
            }
        }

        public static <T> T readValue(String content, Class<T> valueType) {
            try {
                return OM.readValue(content, valueType);
            } catch (IOException e) {
                throw new BasicException(null, null, e);
            }
        }

        public static <T> T readValue(String content, Class<T> valueType, T defaultValue) {
            try {
                return OM.readValue(content, valueType);
            } catch (IOException e) {
                return defaultValue;
            }
        }

        public static <T> T readValue(File src, TypeReference<T> typeReference) {
            try {
                return OM.readValue(src, typeReference);
            } catch (IOException e) {
                throw new BasicException(null, null, e);
            }
        }

        public static <T> T readValue(File src, Class<T> valueType) {
            try {
                return OM.readValue(src, valueType);
            } catch (IOException e) {
                throw new BasicException(null, null, e);
            }
        }

        public static <T> T readValue(InputStream is, TypeReference<T> typeReference) {
            try {
                return OM.readValue(is, typeReference);
            } catch (IOException e) {
                throw new BasicException(null, null, e);
            }
        }

        public static <T> T readValue(InputStream is, Class<T> valueType) {
            try {
                return OM.readValue(is, valueType);
            } catch (IOException e) {
                throw new BasicException(null, null, e);
            }
        }

        public static <T> T readValue(InputStream is, Class<T> valueType, T defaultValue) {
            try {
                return OM.readValue(is, valueType);
            } catch (IOException e) {
                return defaultValue;
            }
        }

        public static byte[] writeValueAsBytes(Object value) {
            try {
                return OM.writeValueAsBytes(value);
            } catch (JsonProcessingException e) {
                throw new BasicException(null, null, e);
            }
        }

        public static String writeValueAsString(Object value) {
            try {
                return OM.writeValueAsString(value);
            } catch (JsonProcessingException e) {
                throw new BasicException(null, null, e);
            }
        }

        public static void writeField(JsonGenerator jg, String name, String value) {
            if (value != null) {
                try {
                    jg.writeStringField(name, value);
                } catch (IOException e) {
                    throw new BasicException(null, null, e);
                }
            }
        }

        public static void writeField(JsonGenerator jg, String name, Double value) {
            if (value != null) {
                try {
                    jg.writeNumberField(name, value);
                } catch (IOException e) {
                    throw new BasicException(null, null, e);
                }
            }
        }

        public static void writeField(JsonGenerator jg, String name, Float value) {
            if (value != null) {
                try {
                    jg.writeNumberField(name, value);
                } catch (IOException e) {
                    throw new BasicException(null, null, e);
                }
            }
        }

        public static void writeField(JsonGenerator jg, String name, Long value) {
            if (value != null) {
                try {
                    jg.writeNumberField(name, value);
                } catch (IOException e) {
                    throw new BasicException(null, null, e);
                }
            }
        }

        public static void writeField(JsonGenerator jg, String name, Integer value) {
            if (value != null) {
                try {
                    jg.writeNumberField(name, value);
                } catch (IOException e) {
                    throw new BasicException(null, null, e);
                }
            }
        }

        public static void writeField(JsonGenerator jg, String name, Date value, String pattern) {
            if (value != null) {
                try {
                    jg.writeStringField(name, new SimpleDateFormat(pattern).format(value));
                } catch (IOException e) {
                    throw new BasicException(null, null, e);
                }
            }
        }
    }

    public static final class Fast extends JSON {

        public static <T> T readValue(File content, Class<T> valueType) {
            try (InputStream is = FileUtils.openInputStream(content)) {
                return readValue(is, valueType);
            } catch (IOException e) {
                throw new BasicException(null, null, e);
            }
        }

        public static <T> T readValue(InputStream is, Class<T> valueType) {
            try (InputStreamReader isr = new InputStreamReader(is, ByteUtils.CHARSET_NAME);
                 JSONReader jr = new JSONReader(isr)) {
                return jr.readObject(valueType);
            } catch (UnsupportedEncodingException e) {
                throw new BasicException(null, null, e);
            } catch (IOException e) {
                throw new BasicException(null, null, e);
            }
        }

        public static void writeValue(File content, Object value) {
            try (OutputStream os = FileUtils.openOutputStream(content)) {
                writeValue(os, value);
            } catch (IOException e) {
                throw new BasicException(null, null, e);
            }
        }

        public static void writeValue(OutputStream os, Object value) {
            try (OutputStreamWriter osw = new OutputStreamWriter(os, ByteUtils.CHARSET_NAME);
                 JSONWriter jw = new JSONWriter(osw)) {
                jw.writeValue(value);
            } catch (UnsupportedEncodingException e) {
                throw new BasicException(null, null, e);
            } catch (IOException e) {
                throw new BasicException(null, null, e);
            }
        }

        public static <T> T select(JSONObject json, String xpath, Class<T> clazz) {
            if (json == null) {
                return null;
            }
            if (StringUtils.contains(xpath, ".")) {
                String name = StringUtils.substringBefore(xpath, ".");
                json = json.getJSONObject(name);
                xpath = StringUtils.substringAfter(xpath, ".");
                return select(json, xpath, clazz);
            } else {
                return json.getObject(xpath, clazz);
            }
        }
    }

    public static final class Resolver {

        private static final Logger logger = LoggerFactory.getLogger(Resolver.class);

        @Value("${basic.default.locale}")
        private String locale;
        @Value("${basic.default.timezone}")
        private String timezone;

        @PostConstruct
        public void init() {
            if (logger.isInfoEnabled()) {
                logger.info("using locale '" + locale + "'");
            }
            Jackson.OM.setLocale(org.springframework.util.StringUtils.parseLocale(locale));
            if (logger.isInfoEnabled()) {
                logger.info("using timezone '" + timezone + "'");
            }
            Jackson.OM.setTimeZone(TimeZone.getTimeZone(timezone));
        }
    }
}
