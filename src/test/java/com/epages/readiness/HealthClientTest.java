package com.epages.readiness;

import static org.assertj.core.api.BDDAssertions.then;
import static org.springframework.boot.actuate.health.Status.DOWN;
import static org.springframework.boot.actuate.health.Status.UNKNOWN;
import static org.springframework.boot.actuate.health.Status.UP;

import java.util.List;

import java.util.Map;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringRunner;

import com.epages.readiness.HealthResponse.ChildStatus;

@ReadinessApplicationTest
@RunWith(SpringRunner.class)
public class HealthClientTest {

    @Rule
    @Autowired
    public MockRestTemplateRule mockRestTemplateRule;

    @Autowired
    private ReadinessSettings settings;

    @Autowired
    private HealthClient healthClient;

    @Test
    public void should_get_health() {
        // GIVEN
        HealthRequest request = settings.getServices().get(0);

        // WHEN
        HealthResponse healthResponse = healthClient.getHealth(request);

        // THEN
        then(healthResponse.getStatus()).isNotNull();
    }

    @Test
    public void should_get_children_status() {
        // GIVEN
        HealthResponse healthResponse = HealthResponse.builder()
                .status(DOWN)
                .request(settings.getServices().get(0))
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
    public void should_handle_exception() {
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
