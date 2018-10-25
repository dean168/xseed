package org.learning.basic.web.client;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.lang.Nullable;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestOperations;

import java.net.URI;
import java.util.Map;

public interface IRestOperations extends RestOperations {

    @Nullable
    <T> T getForObject(String url, ParameterizedTypeReference<T> responseType, Object... uriVariables) throws RestClientException;

    @Nullable
    <T> T getForObject(String url, ParameterizedTypeReference<T> responseType, Map<String, ?> uriVariables) throws RestClientException;

    @Nullable
    <T> T getForObject(URI url, ParameterizedTypeReference<T> responseType) throws RestClientException;

    @Nullable
    <T> T postForObject(String url, @Nullable Object request, ParameterizedTypeReference<T> responseType, Map<String, ?> uriVariables) throws RestClientException;

    @Nullable
    <T> T postForObject(String url, @Nullable Object request, ParameterizedTypeReference<T> responseType, Object... uriVariables) throws RestClientException;

    @Nullable
    <T> T postForObject(URI url, @Nullable Object request, ParameterizedTypeReference<T> responseType) throws RestClientException;
}
