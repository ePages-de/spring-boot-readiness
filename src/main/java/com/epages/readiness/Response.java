package com.epages.readiness;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(NON_NULL)
@JsonPropertyOrder(value = {"status", "platform", "uri", "totalTimeMillis", "description"}, alphabetic = true)
public interface Response extends StatusCheck {
    Long getTotalTimeMillis();
}
