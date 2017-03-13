package com.epages.readiness;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import org.springframework.boot.actuate.health.Status;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import lombok.Builder;
import lombok.Getter;
import lombok.Singular;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;
import static org.springframework.boot.actuate.health.Status.DOWN;
import static org.springframework.boot.actuate.health.Status.UP;

@Getter
@Builder
public class ReadinessResponse implements Response {

    private String platform;

    private Long totalTimeMillis;

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
        boolean allUp = children.stream()
                .map(HealthResponse::getStatus)
                .allMatch(UP::equals);
        return allUp ? UP : DOWN;
    }

    static class ReadinessResponseBuilder {
        ReadinessResponseBuilder combine(ReadinessResponseBuilder other) {
            return children(other.children);
        }
    }
}
