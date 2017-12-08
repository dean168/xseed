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
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.learning.basic.core.BasicException;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public abstract class JsonUtils extends StatusUtils {

	private static final JsonUtils JU = new JsonUtils() {
		@Override
		protected JsonGenerator _createGenerator(OutputStream os) throws IOException {
			return Jackson.JF.createGenerator(os, JsonEncoding.UTF8);
		}
	};

	public static void writeStatus(OutputStream os, boolean status, String message) {
		JU._writeStatus(os, status, message);
	}

	public static void writeStatus(OutputStream os, boolean status, String message, Map<String, String> props) {
		JU._writeStatus(os, status, message, props);
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

	    public static <T> T readValue(String content, TypeReference<T> jsonTypeReference) {
	    	try {
                return OM.readValue(content, jsonTypeReference);
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
		    InputStream is = null;
			try {
				return readValue(is = FileUtils.openInputStream(content), valueType);
			} catch (IOException e) {
				throw new BasicException(null, null, e);
            } finally {
				IOUtils.closeQuietly(is);
			}
		}

        public static <T> T readValue(InputStream is, Class<T> valueType) {
            InputStreamReader isr = null;
            JSONReader jr = null;
            try {
                return (jr = new JSONReader(isr = new InputStreamReader(is, ByteUtils.CHARSET_NAME))).readObject(valueType);
            } catch (UnsupportedEncodingException e) {
				throw new BasicException(null, null, e);
            } finally {
                IOUtils.closeQuietly(jr);
                IOUtils.closeQuietly(isr);
            }
        }

		public static void writeValue(File content, Object value) {
			OutputStream os = null;
			try {
				writeValue(os = FileUtils.openOutputStream(content), value);
			} catch (IOException e) {
				throw new BasicException(null, null, e);
            } finally {
				IOUtils.closeQuietly(os);
			}
		}

		public static void writeValue(OutputStream os, Object value) {
		    OutputStreamWriter osw = null;
			JSONWriter jw = null;
			try {
				(jw = new JSONWriter(osw = new OutputStreamWriter(os, ByteUtils.CHARSET_NAME))).writeValue(value);
			} catch (UnsupportedEncodingException e) {
				throw new BasicException(null, null, e);
            } finally {
				IOUtils.closeQuietly(jw);
				IOUtils.closeQuietly(osw);
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
}
