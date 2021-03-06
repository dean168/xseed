package org.learning.basic.web.clients.support;

import org.learning.basic.utils.JsonUtils.Jackson;
import org.learning.basic.web.clients.IRestOperations;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;
import org.springframework.web.client.HttpMessageConverterExtractor;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.List;
import java.util.Map;

public class RestTemplateImpl extends RestTemplate implements IRestOperations {

    public RestTemplateImpl() {
        super();
        jacksons();
    }

    public RestTemplateImpl(ClientHttpRequestFactory requestFactory) {
        super(requestFactory);
        jacksons();
    }

    public RestTemplateImpl(List<HttpMessageConverter<?>> messageConverters) {
        super(messageConverters);
        jacksons();
    }

    protected void jacksons() {
        getMessageConverters().forEach(converter -> {
            if (MappingJackson2HttpMessageConverter.class.isAssignableFrom(ClassUtils.getUserClass(converter.getClass()))) {
                ((MappingJackson2HttpMessageConverter) converter).setObjectMapper(Jackson.OM);
            }
        });
    }

    @Override
    @Nullable
    public <T> T getForObject(String url, ParameterizedTypeReference<T> responseType, Object... uriVariables) throws RestClientException {
        RequestCallback callback = httpEntityCallback(null, responseType.getType());
        HttpMessageConverterExtractor<T> extractor = new HttpMessageConverterExtractor<>(responseType.getType(), getMessageConverters());
        return execute(url, HttpMethod.GET, callback, extractor, uriVariables);
    }

    @Override
    @Nullable
    public <T> T getForObject(String url, ParameterizedTypeReference<T> responseType, Map<String, ?> uriVariables) throws RestClientException {
        RequestCallback callback = httpEntityCallback(null, responseType.getType());
        HttpMessageConverterExtractor<T> extractor = new HttpMessageConverterExtractor<>(responseType.getType(), getMessageConverters());
        return execute(url, HttpMethod.GET, callback, extractor, uriVariables);
    }

    @Override
    @Nullable
    public <T> T getForObject(URI url, ParameterizedTypeReference<T> responseType) throws RestClientException {
        RequestCallback callback = httpEntityCallback(null, responseType.getType());
        HttpMessageConverterExtractor<T> extractor = new HttpMessageConverterExtractor<>(responseType.getType(), getMessageConverters());
        return execute(url, HttpMethod.GET, callback, extractor);
    }

    @Override
    @Nullable
    public <T> T postForObject(String url, Object request, ParameterizedTypeReference<T> responseType, Map<String, ?> uriVariables) throws RestClientException {
        RequestCallback callback = httpEntityCallback(request, responseType.getType());
        HttpMessageConverterExtractor<T> extractor = new HttpMessageConverterExtractor<>(responseType.getType(), getMessageConverters());
        return execute(url, HttpMethod.POST, callback, extractor, uriVariables);
    }

    @Override
    @Nullable
    public <T> T postForObject(String url, Object request, ParameterizedTypeReference<T> responseType, Object... uriVariables) throws RestClientException {
        RequestCallback callback = httpEntityCallback(request, responseType.getType());
        HttpMessageConverterExtractor<T> extractor = new HttpMessageConverterExtractor<>(responseType.getType(), getMessageConverters());
        return execute(url, HttpMethod.POST, callback, extractor, uriVariables);
    }

    @Override
    @Nullable
    public <T> T postForObject(URI url, Object request, ParameterizedTypeReference<T> responseType) throws RestClientException {
        RequestCallback callback = httpEntityCallback(request, responseType.getType());
        HttpMessageConverterExtractor<T> extractor = new HttpMessageConverterExtractor<>(responseType.getType(), getMessageConverters());
        return execute(url, HttpMethod.POST, callback, extractor);
    }
}
