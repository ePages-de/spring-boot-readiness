package com.epages.readiness;

import java.net.URI;
import java.util.List;

import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.boot.actuate.health.Status;
import org.springframework.web.client.RestClientException;
import org.springframework.web.util.UriComponentsBuilder;

import com.google.common.collect.Iterables;

class MockRestTemplateAnswer implements Answer {
    @Override
    public HealthResponse answer(InvocationOnMock invocation) {
        URI uri = invocation.getArgumentAt(0, URI.class);
        List<String> pathSegments = UriComponentsBuilder.fromUri(uri).build().getPathSegments();
        String status = Iterables.getLast(pathSegments);
        if ("EXCEPTION".equals(status)) {
            throw new RestClientException("simulated exception");
        }
        return HealthResponse.builder().status(new Status(status)).build();
    }
}
