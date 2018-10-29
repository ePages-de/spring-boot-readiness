package com.epages.readiness;

import org.springframework.boot.actuate.health.Status;

import com.fasterxml.jackson.annotation.JsonIgnore;

public interface StatusCheck {

    Status getStatus();

    @JsonIgnore
    default String getCssClass() {
        switch (getStatus().getCode()) {
            case "UP":
                return "success";
            case "DEGRADED":
                return "warning";
            default:
                return "danger";
        }
    }

    @JsonIgnore
    default String getGlyphIcon() {
        switch (getStatus().getCode()) {
            case "UP":
                return "glyphicon-ok";
            case "DEGRADED":
                return "glyphicon-warning-sign";
            default:
                return "glyphicon-remove";
        }
    }
}
