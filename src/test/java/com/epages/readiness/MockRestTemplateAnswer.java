package com.epages.readiness;

import java.net.URI;
import java.util.List;

import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.boot.actuate.health.Status;
import org.springframework.web.client.RestClientException;
import org.springframework.web.util.UriComponentsBuilder;

class MockRestTemplateAnswer implements Answer<HealthResponse> {
    @Override
    public HealthResponse answer(InvocationOnMock invocation) {
        URI uri = invocation.getArgument(0);
        List<String> pathSegments = UriComponentsBuilder.fromUri(uri).build().getPathSegments();
        String status = pathSegments.get(pathSegments.size() - 1);
        if ("EXCEPTION".equals(status)) {
            throw new RestClientException("simulated exception");
        }
        return HealthResponse.builder().status(new Status(status)).build();
    }
}
