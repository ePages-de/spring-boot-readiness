package com.epages.readiness;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.willAnswer;

import java.net.URI;

import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class MockRestTemplateExtension implements BeforeEachCallback {

    private final RestTemplate mockRestTemplate;

    @Override
    public void beforeEach(ExtensionContext context) {
        willAnswer(new MockRestTemplateAnswer()).given(mockRestTemplate).getForObject(any(URI.class), any());
    }

}
