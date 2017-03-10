package com.epages.readiness;

import java.security.cert.X509Certificate;
import java.util.List;

import javax.net.ssl.SSLContext;

import org.apache.http.client.HttpClient;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.ssl.TrustStrategy;
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
        return configure(HttpClients.custom(), userAgent)
                .setSSLSocketFactory(new SSLConnectionSocketFactory(sslContext))
                .build();
    }

    @Bean("httpClient")
    @Profile("!insecure")
    public HttpClient secureHttpClient(@Value("${spring.application.name}") String userAgent) {
        return configure(HttpClients.custom(), userAgent).build();
    }

    private HttpClientBuilder configure(HttpClientBuilder builder, String userAgent) {
        return builder
                .setUserAgent(userAgent)
                .disableAutomaticRetries()
                .setMaxConnTotal(settings.getServices().size())
                .setMaxConnPerRoute(settings.getServices().size());
    }

    @Bean
    public ClientHttpRequestFactory clientHttpRequestFactory(HttpClient httpClient) {
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(httpClient);
        factory.setConnectTimeout(settings.getConnectionTimeout());
        factory.setReadTimeout(settings.getReadTimeout());
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
