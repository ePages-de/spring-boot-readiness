package com.epages.readiness;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.springframework.boot.actuate.health.Status;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Singular;
import lombok.ToString;

import static com.google.common.collect.Maps.newLinkedHashMap;
import static java.util.stream.Collectors.toList;
import static lombok.AccessLevel.PRIVATE;
import static org.springframework.boot.actuate.health.Status.UP;

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

    @Singular
    @Getter(onMethod = @__(@JsonAnyGetter))
    private Map<String, Object> children = newLinkedHashMap();

    @JsonCreator
    public HealthResponse(@JsonProperty("status") String status) {
        this.status = new Status(status);
    }

    @JsonAnySetter
    public void addChild(String name, Object child) {
        children.put(name, child);
    }

    @JsonIgnore
    public List<ChildStatus> getChildrenStatus() {
        return children.entrySet().stream()
                .map(ChildStatus::new)
                .collect(toList());
    }

    @Getter
    @RequiredArgsConstructor
    static class ChildStatus {
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

        public boolean isUp() {
            return UP.equals(status);
        }
    }
}
