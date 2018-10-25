package org.learning.basic.utils;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

public abstract class XmlUtils {

	private static final ConcurrentMap<Class<?>, JAXBContext> jaxbContexts = new ConcurrentHashMap<>(64);

	protected static JAXBContext getJaxbContext(Class<?> clazz) {
		Assert.notNull(clazz, "'clazz' must not be null");
		JAXBContext jaxbContext = jaxbContexts.get(clazz);
		if (jaxbContext == null) {
			try {
				jaxbContext = jaxbContexts.putIfAbsent(clazz, JAXBContext.newInstance(clazz));
			}
			catch (JAXBException ex) {
				throw new HttpMessageConversionException(
						"Could not instantiate JAXBContext for class [" + clazz + "]: " + ex.getMessage(), ex);
			}
		}
		return jaxbContext;
	}

	@SuppressWarnings("unchecked")
	public static <T> T jaxbRead(InputStream is, Class<T> clazz) {
		JAXBContext jaxbContext = getJaxbContext(clazz);
		try {
			Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
			return (T) unmarshaller.unmarshal(is);
		} catch (JAXBException e) {
			throw new RuntimeException(e);
		}
	}

	public static void jaxbWrite(OutputStream os, Object entity) {
		Class<?> clazz = ClassUtils.getUserClass(entity);
		JAXBContext jaxbContext = getJaxbContext(clazz);
		try {
			Marshaller marshaller = jaxbContext.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_ENCODING, ByteUtils.CHARSET_NAME);
			marshaller.marshal(entity, os);
		} catch (JAXBException e) {
			throw new RuntimeException(e);
		}
	}

	public static String toJaxbString(Object entity) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		XmlUtils.jaxbWrite(bos, entity);
		return bos.toString();
	}
}
