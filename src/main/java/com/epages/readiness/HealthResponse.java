package com.epages.readiness;

import static com.google.common.collect.Maps.newLinkedHashMap;
import static java.util.Collections.emptyMap;
import static java.util.stream.Collectors.toList;
import static lombok.AccessLevel.PRIVATE;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import org.springframework.boot.actuate.health.Status;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@Builder(toBuilder = true)
@ToString(of = {"request", "status"})
@AllArgsConstructor(access = PRIVATE)
public class HealthResponse implements Response {

    @JsonProperty("uri")
    private HealthRequest request;

    private Status status;

    private Long totalTimeMillis;

    @JsonIgnore
    public String getService() {
        return request.getService();
    }

    @Builder.Default
    private Map<String, Object> details = newLinkedHashMap();

    @JsonCreator
    public HealthResponse(
            @JsonProperty("status") String status,
            @JsonProperty("details") Map<String, Object> details) {
        this.status = new Status(status);
        this.details = details == null ? emptyMap() : details;
    }

    @JsonIgnore
    @SuppressWarnings("unchecked")
    public List<ChildStatus> getChildrenStatus() {
        return details.entrySet().stream()
                .map(ChildStatus::new)
                .collect(toList());
    }

    @Getter
    @RequiredArgsConstructor
    static class ChildStatus implements StatusCheck {
        private final String name;

        private final Status status;

        ChildStatus(Entry<String, Object> entry) {
            this(entry.getKey(), entry.getValue());
        }

        ChildStatus(String name, Object object) {
            this(name, (object instanceof Map ? Optional.of((Map) object) : Optional.empty()));
        }

        ChildStatus(String name, Optional<Map> optionalMap) {
            this(name, optionalMap.map(map -> map.get("status")).orElse("custom").toString());
        }

        ChildStatus(String name, String status) {
            this(name, new Status(status));
        }
    }
}
