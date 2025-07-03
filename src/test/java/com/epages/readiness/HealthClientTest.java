package com.epages.readiness;

import static org.assertj.core.api.BDDAssertions.then;
import static org.springframework.boot.actuate.health.Status.DOWN;
import static org.springframework.boot.actuate.health.Status.UNKNOWN;
import static org.springframework.boot.actuate.health.Status.UP;

import java.util.List;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.epages.readiness.HealthResponse.ChildStatus;

@ReadinessApplicationTest
@ExtendWith(SpringExtension.class)
class HealthClientTest {

    @RegisterExtension
    @Autowired
    private MockRestTemplateExtension mockRestTemplateExtension;

    @Autowired
    private ReadinessSettings settings;

    @Autowired
    private HealthClient healthClient;

    @Test
    void should_get_health() {
        // GIVEN
        HealthRequest request = settings.getServices().getFirst();

        // WHEN
        HealthResponse healthResponse = healthClient.getHealth(request);

        // THEN
        then(healthResponse.getStatus()).isNotNull();
    }

    @Test
    void should_get_children_status() {
        // GIVEN
        HealthResponse healthResponse = HealthResponse.builder()
                .status(DOWN)
                .request(settings.getServices().getFirst())
                .details(Map.of(
                        "foo", Map.of("status", UP),
                        "bar", Map.of("status", UNKNOWN)
                        )
                )
                .build();

        // WHEN
        List<ChildStatus> childrenStatus = healthResponse.getChildrenStatus();

        // THEN
        then(childrenStatus).isNotEmpty();
        then(childrenStatus).extracting(ChildStatus::getName).containsOnly("foo", "bar");
        then(childrenStatus).extracting(ChildStatus::getStatus).containsOnly(UP, UNKNOWN);
    }

    @Test
    void should_handle_exception() {
        // GIVEN
        HealthRequest request = new HealthRequest("exception", "https://host.invalid/EXCEPTION");

        // WHEN
        HealthResponse response = healthClient.getHealth(request);

        // THEN
        then(response.getStatus()).isEqualTo(DOWN);
        then(response.getService()).isEqualTo(request.getService());
        then(response.getChildrenStatus()).isEmpty();
    }
}
