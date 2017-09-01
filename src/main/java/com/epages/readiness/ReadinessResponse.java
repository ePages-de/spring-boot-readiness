package com.epages.readiness;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;
import static lombok.AccessLevel.NONE;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthAggregator;
import org.springframework.boot.actuate.health.OrderedHealthAggregator;
import org.springframework.boot.actuate.health.Status;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Builder;
import lombok.Getter;
import lombok.Singular;

@Getter
@Builder
public class ReadinessResponse implements Response {

    private String platform;

    private Long totalTimeMillis;

    @Getter(value = NONE)
    private HealthAggregator healthAggregator;

    @JsonIgnore
    @Singular("child")
    private List<HealthResponse> children;

    @JsonAnyGetter
    public Map<String, HealthResponse> serializedChildren() {
        return children.stream()
                .collect(toMap(HealthResponse::getService, identity(), (first, second) -> second, LinkedHashMap::new));
    }

    @Override
    public Status getStatus() {
        Map<String, Health> healths = children.stream()
                .collect(toMap(
                        HealthResponse::getService,
                        healthResponse -> new Health.Builder().status(healthResponse.getStatus()).build()
                ));

        return healthAggregator.aggregate(healths).getStatus();
    }

    static class ReadinessResponseBuilder {
        // default needed for Lombok.
        // Unfortunately Lombok's @Builder.Default breaks Jackson deserialization of HealthResponse
        private HealthAggregator healthAggregator = new OrderedHealthAggregator();
        ReadinessResponseBuilder combine(ReadinessResponseBuilder other) {
            return children(other.children);
        }
    }
}
