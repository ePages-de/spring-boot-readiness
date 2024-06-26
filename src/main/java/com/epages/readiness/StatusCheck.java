package com.epages.readiness;

import org.springframework.boot.actuate.health.Status;

import com.fasterxml.jackson.annotation.JsonIgnore;

public interface StatusCheck {

    Status getStatus();

    @JsonIgnore
    default String getCssClass() {
        return switch (getStatus().getCode()) {
            case "UP" -> "success";
            case "DEGRADED" -> "warning";
            default -> "danger";
        };
    }
}
