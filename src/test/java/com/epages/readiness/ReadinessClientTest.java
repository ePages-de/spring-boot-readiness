package com.epages.readiness;

import static org.assertj.core.api.BDDAssertions.then;
import static org.springframework.boot.actuate.health.Status.DOWN;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringRunner;

@ReadinessApplicationTest
@RunWith(SpringRunner.class)
public class ReadinessClientTest {

    @Rule
    @Autowired
    public MockRestTemplateRule mockRestTemplateRule;

    @Autowired
    private ReadinessClient readinessClient;

    @Autowired
    private ReadinessSettings settings;

    @Test
    public void should_retrieve_health_checks() {
        // WHEN
        ReadinessResponse response = readinessClient.getReadiness();

        // THEN
        then(response.getStatus()).isEqualTo(DOWN);
        then(response.getChildren()).hasSize(settings.getServices().size());
    }
}
