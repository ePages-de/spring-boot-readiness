package com.epages.readiness;

import static lombok.AccessLevel.PACKAGE;

import java.net.URI;
import java.util.Comparator;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonValue;
import com.google.common.annotations.VisibleForTesting;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString(of = {"service"})
@NoArgsConstructor
@AllArgsConstructor(access = PACKAGE, onConstructor = @__(@VisibleForTesting))
public class HealthRequest implements Comparable<HealthRequest> {
    @JsonIgnore
    private String service;

    @Getter(onMethod = @__(@JsonValue))
    private URI uri;

    @Override
    public int compareTo(HealthRequest that) {
        return Comparator.comparing(HealthRequest::getService).compare(this, that);
    }
}
