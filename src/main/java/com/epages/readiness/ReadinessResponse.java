package com.epages.readiness;

import static org.springframework.boot.actuate.health.Status.DOWN;
import static org.springframework.boot.actuate.health.Status.UP;

import java.util.Map;

import org.springframework.boot.actuate.health.Status;

import com.fasterxml.jackson.annotation.JsonAnyGetter;

import lombok.Builder;
import lombok.Getter;
import lombok.Singular;

@Getter
@Builder
public class ReadinessResponse implements Response {

    private String platform;

    private Long totalTimeMillis;

    @Singular("child")
    @Getter(onMethod = @__(@JsonAnyGetter))
    private Map<String, Response> children;

    @Override
    public Status getStatus() {
        boolean allUp = children.values().stream()
                .map(Response::getStatus)
                .allMatch(UP::equals);
        return allUp ? UP : DOWN;
    }

    static class ReadinessResponseBuilder {
        ReadinessResponseBuilder combine(ReadinessResponseBuilder other) {
            for (int i = 0; i < other.children$key.size(); i++) {
                child(other.children$key.get(i), other.children$value.get(i));
            }
            return this;
        }
    }
}
