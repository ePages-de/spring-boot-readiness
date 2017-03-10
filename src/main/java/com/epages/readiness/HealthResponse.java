package com.epages.readiness;

import static com.google.common.collect.Maps.newLinkedHashMap;
import static java.util.Comparator.comparingLong;
import static java.util.stream.Collectors.toList;
import static lombok.AccessLevel.PRIVATE;
import static org.springframework.boot.actuate.health.Status.DOWN;
import static org.springframework.boot.actuate.health.Status.OUT_OF_SERVICE;
import static org.springframework.boot.actuate.health.Status.UNKNOWN;
import static org.springframework.boot.actuate.health.Status.UP;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import org.springframework.boot.actuate.health.Status;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Singular;
import lombok.ToString;

@Getter
@Builder(toBuilder = true)
@ToString(of = {"request", "status"})
@AllArgsConstructor(access = PRIVATE)
public class HealthResponse implements Comparable<HealthResponse>, Response {

    private static final Comparator<HealthResponse> COMPARATOR = Comparator
            .comparing(HealthResponse::getStatus, new StatusComparator())
            .thenComparing(comparingLong(HealthResponse::getTotalTimeMillis).reversed())
            .thenComparing(HealthResponse::getRequest);

    @JsonProperty("uri")
    private HealthRequest request;

    private Status status;

    private Long totalTimeMillis;

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

    @Override
    public int compareTo(HealthResponse that) {
        return COMPARATOR.compare(this, that);
    }

    @JsonIgnore
    public List<ChildStatus> getChildrenStatus() {
        return children.entrySet().stream()
                .map(ChildStatus::new)
                .collect(toList());
    }

    @Getter
    static class ChildStatus {
        private final String name;

        private final String status;

        ChildStatus(Entry<String, Object> entry) {
            this(entry.getKey(), entry.getValue());
        }

        ChildStatus(String name, Object object) {
            this(name, (object instanceof Map ? Optional.of((Map) object) : Optional.empty()));
        }

        @SuppressWarnings("unchecked")
        ChildStatus(String name, Optional<Map> map) {
            this.name = name;
            this.status = map.map(m -> m.get("status")).orElse(UNKNOWN).toString();
        }
    }

    private static class StatusComparator implements Comparator<Status> {

        private final List<Status> statusOrder = ImmutableList.of(DOWN, OUT_OF_SERVICE, UNKNOWN, UP);

        @Override
        public int compare(Status s1, Status s2) {
            int i1 = this.statusOrder.indexOf(s1);
            int i2 = this.statusOrder.indexOf(s2);
            return (i1 < i2 ? -1 : (i1 == i2 ? s1.getCode().compareTo(s2.getCode()) : 1));
        }
    }
}
