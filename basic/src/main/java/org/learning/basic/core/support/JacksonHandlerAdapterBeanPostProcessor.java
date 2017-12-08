package org.learning.basic.core.support;

import org.learning.basic.utils.JsonUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

/**
 * 更改 MappingJackson2HttpMessageConverter 的 ObjectMapper 配置
 */
public class JacksonHandlerAdapterBeanPostProcessor implements BeanPostProcessor {

	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		return bean;
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {

		if (bean instanceof RequestMappingHandlerAdapter) {

			RequestMappingHandlerAdapter adapter = (RequestMappingHandlerAdapter) bean;

			adapter.getMessageConverters().forEach((HttpMessageConverter<?> converter) -> {
				if (converter instanceof MappingJackson2HttpMessageConverter) {
					MappingJackson2HttpMessageConverter converterToUse = (MappingJackson2HttpMessageConverter) converter;
					converterToUse.setObjectMapper(JsonUtils.Jackson.OM);
				}
			});
		}

		return bean;
	}

}
