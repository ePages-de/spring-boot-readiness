package com.epages.readiness;

import static com.epages.readiness.Timer.startTiming;
import static com.epages.readiness.Timer.stopTiming;
import static com.epages.readiness.sort.SortConfiguration.SORT_BY_SERVICE;

import java.util.Comparator;

import org.springframework.boot.actuate.health.HealthAggregator;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import com.epages.readiness.ReadinessResponse.ReadinessResponseBuilder;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ReadinessClient {

    private final ReadinessSettings settings;

    private final HealthClient healthClient;

    private final HealthAggregator healthAggregator;

    public ReadinessResponse getReadiness() {
        return getReadiness(SORT_BY_SERVICE);
    }

    public ReadinessResponse getReadiness(Comparator<HealthResponse> healthResponseComparator) {
        StopWatch stopWatch = startTiming("readiness");
        return settings.getServices().stream()
                .parallel() // yes, we're sending multiple health requests in parallel
                .map(healthClient::getHealth)
                .sorted(healthResponseComparator)
                .collect(ReadinessResponseBuilder::new, ReadinessResponseBuilder::child, ReadinessResponseBuilder::combine)
                .healthAggregator(healthAggregator)
                .totalTimeMillis(stopTiming(stopWatch))
                .platform(settings.getPlatform())
                .build();
    }
}
