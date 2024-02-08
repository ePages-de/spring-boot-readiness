package com.epages.readiness;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@Data
@ConfigurationProperties(prefix = "readiness")
public class ReadinessSettings {
    private String platform;

    private List<HealthRequest> services = new ArrayList<>();

    private int connectionTimeout = 5_000;

    private int readTimeout = 5_000;

    private int connectionRequestTimeout = 5_000;
}
