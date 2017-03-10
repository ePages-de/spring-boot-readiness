package com.epages.readiness;

import static com.epages.readiness.Timer.startTiming;
import static com.epages.readiness.Timer.stopTiming;

import java.util.function.BiConsumer;

import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import com.epages.readiness.ReadinessResponse.ReadinessResponseBuilder;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ReadinessClient {

    private final ReadinessSettings settings;

    private final HealthClient healthClient;

    public ReadinessResponse getReadiness() {
        StopWatch stopWatch = startTiming("readiness");
        return settings.getServices().stream()
                .parallel() // yes, we're sending multiple health requests in parallel
                .map(healthClient::getHealth)
                .sorted()
                .collect(ReadinessResponseBuilder::new, accumulator(), ReadinessResponseBuilder::combine)
                .totalTimeMillis(stopTiming(stopWatch))
                .platform(settings.getPlatform())
                .build();
    }

    private BiConsumer<ReadinessResponseBuilder, HealthResponse> accumulator() {
        return (readinessResponseBuilder, healthResponse) ->
                readinessResponseBuilder.child(healthResponse.getRequest().getService(), healthResponse);
    }
}
