package org.learning.basic.web.clients.support;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpRequest;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.protocol.HttpContext;
import org.learning.basic.core.SessionContext;
import org.learning.basic.utils.RSAUtils;
import org.learning.basic.web.clients.IHCRequestFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;

import javax.annotation.PostConstruct;

public class HCRequestFactoryImpl extends HttpComponentsClientHttpRequestFactory implements IHCRequestFactory {

    private static final Logger logger = LoggerFactory.getLogger(HCRequestFactoryImpl.class);

    protected PoolingHttpClientConnectionManager phccmanager;
    protected CloseableHttpClient httpclient;

    protected int maxTotal = 10000;
    protected int defaultMaxPerRoute = 100;
    protected int requestTimeout = 1000 * 10;
    protected int connectTimeout = 1000 * 10;
    protected int socketTimeout = 1000 * 60;
    protected boolean redirectsEnabled = true;

//    @Value("${basic.shiro.tokens.accessKey}")
    private String accessKey;
//    @Value("${basic.shiro.tokens.headerName}")
    private String headerName;

    @PostConstruct
    public void init() {
        phccmanager = createConnectionManager();
        httpclient = createHCBuilder().build();
        setHttpClient(httpclient);
    }

    protected PoolingHttpClientConnectionManager createConnectionManager() {
        PoolingHttpClientConnectionManager manager = new PoolingHttpClientConnectionManager();
        manager.setMaxTotal(maxTotal);
        manager.setDefaultMaxPerRoute(defaultMaxPerRoute);
        return manager;
    }

    protected HttpClientBuilder createHCBuilder() {
        HttpClientBuilder builder = HttpClientBuilder.create();
        builder.setConnectionManager(phccmanager);
//		builder.setRetryHandler(new DefaultHttpRequestRetryHandler());
//		builder.setDefaultCookieStore(new BasicCookieStore());
        builder.setDefaultRequestConfig(createRCBuilder().build());
        builder.addInterceptorLast((HttpRequest request, HttpContext context) -> {
            SessionContext ctx = SessionContext.get();
            if (ctx != null && StringUtils.isNotEmpty(ctx.accountId())) {
                request.removeHeaders(headerName);
                request.setHeader(headerName, RSAUtils.encrypt(accessKey, ctx.accountId()));
            } else if (logger.isDebugEnabled()) {
                logger.debug("account id not found from session context.");
            }
        });
        return builder;
    }

    protected RequestConfig.Builder createRCBuilder() {
        RequestConfig.Builder builder = RequestConfig.custom();
        builder.setConnectionRequestTimeout(requestTimeout);
        builder.setConnectTimeout(connectTimeout);
        builder.setSocketTimeout(socketTimeout);
        builder.setRedirectsEnabled(redirectsEnabled);
        return builder;
    }

    @Override
    public HttpClient httpclient() {
        return httpclient;
    }

    @Override
    public void destroy() throws Exception {
        try {
            super.destroy();
        } finally {
            phccmanager.close();
        }
    }

    public void setMaxTotal(int maxTotal) {
        this.maxTotal = maxTotal;
    }

    public void setDefaultMaxPerRoute(int defaultMaxPerRoute) {
        this.defaultMaxPerRoute = defaultMaxPerRoute;
    }

    public void setRequestTimeout(int requestTimeout) {
        this.requestTimeout = requestTimeout;
    }

    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public void setSocketTimeout(int socketTimeout) {
        this.socketTimeout = socketTimeout;
    }

    public void setRedirectsEnabled(boolean redirectsEnabled) {
        this.redirectsEnabled = redirectsEnabled;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public void setHeaderName(String headerName) {
        this.headerName = headerName;
    }
}
