package org.learning.basic.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;
import de.undercouch.bson4jackson.BsonFactory;
import org.learning.basic.core.BasicException;
import org.springframework.http.HttpStatus;

import javax.servlet.ServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public abstract class BsonUtils extends StatusUtils {

    private static final BsonUtils BU = new BsonUtils() {
        @Override
        protected JsonGenerator _createGenerator(OutputStream os) throws IOException {
            return Jackson.JF.createGenerator(os, JsonEncoding.UTF8);
        }
    };

    public static void status(ServletResponse response, boolean status, String message) {
        BU._status(response, status, message);
    }

    public static void status(ServletResponse response, HttpStatus status, String message) {
        BU._status(response, status, message);
    }

    public static void status(ServletResponse response, int status, String message) {
        BU._status(response, status, message);
    }

    public static void status(OutputStream os, boolean status, String message) {
        BU._status(os, status, message);
    }

    public static void status(OutputStream os, HttpStatus status, String message) {
        BU._status(os, status, message);
    }

    public static void status(OutputStream os, int status, String message) {
        BU._status(os, status, message);
    }

    public static final class Jackson {

        public static final BsonFactory JF = new BsonFactory();
        public static final ObjectMapper OM = new ObjectMapper(new BsonFactory());

        static {
            OM.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            OM.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
            OM.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            OM.registerModule(new Hibernate5Module().enable(Hibernate5Module.Feature.FORCE_LAZY_LOADING));
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

        public static JsonParser createParser(InputStream in) {
            try {
                return JF.createParser(in);
            } catch (IOException e) {
                throw new BasicException(null, null, e);
            }
        }

        public static JsonParser createParser(byte[] content) {
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

        public static <T> T readValue(byte[] content, TypeReference<T> jsonTypeReference) {
            try {
                return OM.readValue(content, jsonTypeReference);
            } catch (IOException e) {
                throw new BasicException(null, null, e);
            }
        }

        public static <T> T readValue(byte[] content, Class<T> valueType) {
            try {
                return OM.readValue(content, valueType);
            } catch (IOException e) {
                throw new BasicException(null, null, e);
            }
        }

        public static <T> T readValue(byte[] content, Class<T> valueType, T defaultValue) {
            try {
                return OM.readValue(content, valueType);
            } catch (IOException e) {
                return defaultValue;
            }
        }

        public static <T> T readValue(File src, Class<T> valueType) {
            try {
                return OM.readValue(src, valueType);
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
}
