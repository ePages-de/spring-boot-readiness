package com.epages.readiness;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.BDDAssertions.then;
import static org.springframework.boot.actuate.health.Status.UP;

import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.SneakyThrows;

public class HealthResponseTest {

    @Test
    @SneakyThrows
    public void should_deserialize_json() {
        // GIVEN
        ObjectMapper objectMapper = new ObjectMapper();
        //language=JSON
        String json = """
            {
              "details": {
                "db": {
                  "details": {
                    "database": "MySQL",
                    "hello": 1
                  },
                  "status": "UP"
                },
                "hystrix": {
                  "status": "UP"
                },
                "rabbit": {
                  "details": {
                    "version": "3.6.9"
                  },
                  "status": "UP"
                },
                "refreshScope": {
                  "status": "UP"
                }
              },
              "status": "UP"
            }
            """;

        // WHEN
        HealthResponse healthResponse = objectMapper.readValue(json, HealthResponse.class);

        // THEN
        then(healthResponse.getStatus()).isEqualTo(UP);
        then(healthResponse.getChildrenStatus().stream().map(HealthResponse.ChildStatus::getName).collect(toList())).containsOnly("db", "hystrix", "rabbit", "refreshScope");
        then(healthResponse.getChildrenStatus().stream().map(s -> s.getStatus().getCode()).collect(toList())).containsOnly("UP", "UP", "UP", "UP");
    }
}
