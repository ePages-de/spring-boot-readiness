package com.epages.readiness;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

import org.springframework.boot.actuate.health.Status;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static org.springframework.boot.actuate.health.Status.UP;

@JsonInclude(NON_NULL)
@JsonPropertyOrder(value = {"status", "platform", "uri", "totalTimeMillis", "description"}, alphabetic = true)
public interface Response {

    @JsonUnwrapped
    Status getStatus();

    Long getTotalTimeMillis();

    @JsonIgnore
    default boolean isUp() {
        return UP.equals(getStatus());
    }
}
