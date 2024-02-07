package com.epages.readiness;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.web.client.RestTemplate;

@TestConfiguration
class MockRestTemplateConfiguration {
    @MockBean
    private RestTemplate mockRestTemplate;
}
