package org.learning.basic.web.client;

import org.apache.http.client.HttpClient;
import org.springframework.http.client.ClientHttpRequestFactory;

public interface IHCRequestFactory extends ClientHttpRequestFactory {

    String SERVICE_ID = "basic.HCRequestFactory";

    HttpClient httpclient();
}
