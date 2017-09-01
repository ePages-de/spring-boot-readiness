package com.epages.readiness;

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
        String json = "{\n" +
                "    \"db\": {\n" +
                "        \"database\": \"MySQL\",\n" +
                "        \"hello\": 1,\n" +
                "        \"status\": \"UP\"\n" +
                "    },\n" +
                "    \"hystrix\": {\n" +
                "        \"status\": \"UP\"\n" +
                "    },\n" +
                "    \"rabbit\": {\n" +
                "        \"status\": \"UP\",\n" +
                "        \"version\": \"3.6.9\"\n" +
                "    },\n" +
                "    \"refreshScope\": {\n" +
                "        \"status\": \"UP\"\n" +
                "    },\n" +
                "    \"status\": \"UP\"\n" +
                "}";

        // WHEN
        HealthResponse healthResponse = objectMapper.readValue(json, HealthResponse.class);

        // THEN
        then(healthResponse.getStatus()).isEqualTo(UP);
        then(healthResponse.getChildren()).containsKeys("db", "hystrix", "rabbit", "refreshScope");
        then(healthResponse.getChildren().values()).extracting("status").containsOnly("UP", "UP", "UP", "UP");
    }
}
