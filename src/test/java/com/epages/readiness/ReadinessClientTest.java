package com.epages.readiness;

import static org.assertj.core.api.BDDAssertions.then;
import static org.springframework.boot.actuate.health.Status.DOWN;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ReadinessApplicationTest
@ExtendWith(SpringExtension.class)
class ReadinessClientTest {

    @RegisterExtension
    @Autowired
    private MockRestTemplateExtension mockRestTemplateExtension;

    @Autowired
    private ReadinessClient readinessClient;

    @Autowired
    private ReadinessSettings settings;

    @Test
    void should_retrieve_health_checks() {
        // WHEN
        ReadinessResponse response = readinessClient.getReadiness();

        // THEN
        then(response.getStatus()).isEqualTo(DOWN);
        then(response.getChildren()).hasSize(settings.getServices().size());
    }
}
