package com.epages.readiness;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.willAnswer;

import java.net.URI;

import org.junit.rules.ExternalResource;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
class MockRestTemplateRule extends ExternalResource {

    private final RestTemplate mockRestTemplate;

    @Override
    protected void before() {
        willAnswer(new MockRestTemplateAnswer()).given(mockRestTemplate).getForObject(any(URI.class), any());
    }
}
