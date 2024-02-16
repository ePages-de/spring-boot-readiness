package com.epages.readiness;

import static org.assertj.core.api.BDDAssertions.then;
import static org.springframework.boot.actuate.health.Status.DOWN;
import static org.springframework.boot.actuate.health.Status.OUT_OF_SERVICE;
import static org.springframework.boot.actuate.health.Status.UNKNOWN;
import static org.springframework.boot.actuate.health.Status.UP;

import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.actuate.health.SimpleStatusAggregator;
import org.springframework.boot.actuate.health.Status;

public class ReadinessResponseTest {

    private SimpleStatusAggregator healthAggregator;

    @Before
    public void setup() {
        healthAggregator = new SimpleStatusAggregator(
            DOWN, OUT_OF_SERVICE, UNKNOWN, new Status("DEGRADED"), UP
        );
    }

    @Test
    public void should_handle_degraded() {
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
