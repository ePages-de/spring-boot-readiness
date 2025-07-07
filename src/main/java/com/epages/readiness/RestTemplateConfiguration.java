package com.epages.readiness;

import java.security.cert.X509Certificate;
import java.util.List;

import javax.net.ssl.SSLContext;

import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactoryBuilder;
import org.apache.hc.core5.ssl.SSLContexts;
import org.apache.hc.core5.ssl.TrustStrategy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

@Configuration
@RequiredArgsConstructor
public class RestTemplateConfiguration {

    private final ReadinessSettings settings;

    @Bean("httpClient")
    @Profile("insecure")
    @SneakyThrows
    public HttpClient insecureHttpClient(@Value("${spring.application.name}") String userAgent) {
        // http://stackoverflow.com/a/41618092/1393467
        TrustStrategy trustStrategy = (X509Certificate[] chain, String authType) -> true;
        SSLContext sslContext = SSLContexts.custom().loadTrustMaterial(null, trustStrategy).build();

        return configure(
                HttpClients.custom(),
                userAgent,
                SSLConnectionSocketFactoryBuilder.create()
                    .setSslContext(sslContext).build()
        ).build();
    }

    @Bean("httpClient")
    @Profile("!insecure")
    public HttpClient secureHttpClient(@Value("${spring.application.name}") String userAgent) {
        return configure(HttpClients.custom(), userAgent).build();
    }

    private HttpClientBuilder configure(HttpClientBuilder builder, String userAgent) {
        return configure(builder, userAgent, null);
    }

    private HttpClientBuilder configure(HttpClientBuilder builder, String userAgent, SSLConnectionSocketFactory sslSocketFactory) {

        PoolingHttpClientConnectionManagerBuilder connectionManagerBuilder = PoolingHttpClientConnectionManagerBuilder.create()
            .setMaxConnTotal(settings.getServices().size())
            .setMaxConnPerRoute(settings.getServices().size());

        if (sslSocketFactory != null) {
            connectionManagerBuilder.setSSLSocketFactory(sslSocketFactory);
        }

        return builder
                .setUserAgent(userAgent)
                .disableAutomaticRetries()
                .setConnectionManager(connectionManagerBuilder.build());
    }

    @Bean
    public ClientHttpRequestFactory clientHttpRequestFactory(HttpClient httpClient) {
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(httpClient);
        factory.setConnectTimeout(settings.getConnectionTimeout());
        factory.setConnectionRequestTimeout(settings.getConnectionRequestTimeout());
        return factory;
    }

    @Bean
    public RestTemplate restTemplate(ClientHttpRequestFactory requestFactory, List<HttpMessageConverter<?>> messageConverters) {
        RestTemplate restTemplate = new RestTemplate(requestFactory);
        restTemplate.setMessageConverters(messageConverters);
        return restTemplate;
    }
}
