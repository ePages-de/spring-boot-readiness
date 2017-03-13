package com.epages.readiness;

import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ReadinessHealthIndicator extends AbstractHealthIndicator {

    private final ReadinessClient readinessClient;

    @Override
    protected void doHealthCheck(Health.Builder builder) {
        ReadinessResponse readiness = readinessClient.getReadiness();
        builder
                .status(readiness.getStatus())
                .withDetail("platform", readiness.getPlatform())
                .withDetail("totalTimeMillis", readiness.getTotalTimeMillis())
        ;
        readiness.getChildren().forEach(healthResponse ->
                builder.withDetail(healthResponse.getService(), healthResponse));
    }
}
