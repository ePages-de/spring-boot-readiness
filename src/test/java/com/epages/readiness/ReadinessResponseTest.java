package com.epages.readiness;

import static org.assertj.core.api.BDDAssertions.then;
import static org.springframework.boot.actuate.health.Status.DOWN;
import static org.springframework.boot.actuate.health.Status.OUT_OF_SERVICE;
import static org.springframework.boot.actuate.health.Status.UNKNOWN;
import static org.springframework.boot.actuate.health.Status.UP;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.actuate.health.SimpleStatusAggregator;
import org.springframework.boot.actuate.health.Status;

class ReadinessResponseTest {

    private SimpleStatusAggregator healthAggregator;

    @BeforeEach
    void setup() {
        healthAggregator = new SimpleStatusAggregator(
            DOWN, OUT_OF_SERVICE, UNKNOWN, new Status("DEGRADED"), UP
        );
    }

    @Test
    void should_handle_degraded() {
        // GIVEN
        HealthResponse up = HealthResponse.builder()
                .request(new HealthRequest("A", "http://a"))
                .status(UP)
                .build();
        HealthResponse degraded = HealthResponse.builder()
                .request(new HealthRequest("B", "http://b"))
                .status(new Status("DEGRADED"))
                .build();

        ReadinessResponse response = ReadinessResponse.builder()
                .healthAggregator(healthAggregator)
                .child(up)
                .child(degraded)
                .build();

        // WHEN
        Status status = response.getStatus();

        // THEN
        then(status.getCode()).isEqualTo("DEGRADED");
    }
}
