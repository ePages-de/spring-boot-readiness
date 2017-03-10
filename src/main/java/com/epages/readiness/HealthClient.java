package com.epages.readiness;

import static com.epages.readiness.Timer.startTiming;
import static com.epages.readiness.Timer.stopTiming;
import static com.google.common.base.Throwables.getRootCause;
import static org.springframework.boot.actuate.health.Status.DOWN;

import java.net.URI;

import org.springframework.boot.actuate.health.Status;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.epages.readiness.HealthResponse.HealthResponseBuilder;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class HealthClient {

    private final RestTemplate restTemplate;

    public HealthResponse getHealth(HealthRequest healthRequest) {
        StopWatch stopWatch = startTiming(healthRequest.getService());
        return doGetHealth(healthRequest.getUri())
                .totalTimeMillis(stopTiming(stopWatch))
                .request(healthRequest)
                .build();
    }

    private HealthResponseBuilder doGetHealth(URI uri) {
        try {
            log.debug("Checking health at '{}'", uri);
            // TODO let Jackson directly deserialize into HealthResponseBuilder
            return restTemplate.getForObject(uri, HealthResponse.class).toBuilder();
        } catch (RestClientException e) {
            Status status = new Status(DOWN.getCode(), getRootCause(e).getMessage());
            return HealthResponse.builder().status(status);
        }
    }
}
