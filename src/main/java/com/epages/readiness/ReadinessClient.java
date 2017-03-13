package com.epages.readiness;

import com.epages.readiness.ReadinessResponse.ReadinessResponseBuilder;

import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import java.util.Comparator;

import lombok.RequiredArgsConstructor;

import static com.epages.readiness.Timer.startTiming;
import static com.epages.readiness.Timer.stopTiming;
import static com.epages.readiness.sort.SortConfiguration.SORT_BY_SERVICE;

@Component
@RequiredArgsConstructor
public class ReadinessClient {

    private final ReadinessSettings settings;

    private final HealthClient healthClient;

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
                .totalTimeMillis(stopTiming(stopWatch))
                .platform(settings.getPlatform())
                .build();
    }
}
