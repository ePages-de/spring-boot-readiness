package com.epages.readiness;

import java.net.URI;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString(of = {"service"})
@NoArgsConstructor // for ReadinessSettings
public class HealthRequest {
    @JsonIgnore
    private String service;

    @Getter(onMethod = @__(@JsonValue))
    private URI uri;

    @JsonCreator
    public HealthRequest(@JsonProperty("service") String service, @JsonProperty("uri") String uri) {
        this.service = service;
        this.uri = URI.create(uri);
    }
}
