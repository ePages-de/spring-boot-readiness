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
        String json = "{\n" +
                "  \"details\": {\n" +
                "    \"db\": {\n" +
                "      \"details\": {\n" +
                "        \"database\": \"MySQL\",\n" +
                "        \"hello\": 1\n" +
                "      },\n" +
                "      \"status\": \"UP\"\n" +
                "    },\n" +
                "    \"hystrix\": {\n" +
                "      \"status\": \"UP\"\n" +
                "    },\n" +
                "    \"rabbit\": {\n" +
                "      \"details\": {\n" +
                "        \"version\": \"3.6.9\"\n" +
                "      },\n" +
                "      \"status\": \"UP\"\n" +
                "    },\n" +
                "    \"refreshScope\": {\n" +
                "      \"status\": \"UP\"\n" +
                "    }\n" +
                "  },\n" +
                "  \"status\": \"UP\"\n" +
                "}\n";

        // WHEN
        HealthResponse healthResponse = objectMapper.readValue(json, HealthResponse.class);

        // THEN
        then(healthResponse.getStatus()).isEqualTo(UP);
        then(healthResponse.getChildrenStatus().stream().map(HealthResponse.ChildStatus::getName).collect(toList())).containsOnly("db", "hystrix", "rabbit", "refreshScope");
        then(healthResponse.getChildrenStatus().stream().map(s -> s.getStatus().getCode()).collect(toList())).containsOnly("UP", "UP", "UP", "UP");
    }
}
