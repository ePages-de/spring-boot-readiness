package com.epages.readiness;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

import org.springframework.boot.actuate.health.Status;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

@JsonInclude(NON_NULL)
@JsonPropertyOrder(value = {"status", "platform", "uri", "totalTimeMillis", "description"}, alphabetic = true)
public interface Response {

    @JsonUnwrapped
    Status getStatus();

    Long getTotalTimeMillis();
}
