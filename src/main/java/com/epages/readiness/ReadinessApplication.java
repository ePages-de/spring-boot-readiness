package com.epages.readiness;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import com.google.common.collect.ImmutableMap;

import lombok.extern.slf4j.Slf4j;

@Slf4j(topic = "yaml")
@SpringBootApplication
@EnableConfigurationProperties(ReadinessSettings.class)
public class ReadinessApplication {
    public static void main(String[] args) {
        new SpringApplicationBuilder(ReadinessApplication.class).run(args);
    }

    @Bean
    @Profile("cli")
    public CommandLineRunner commandLineRunner(ReadinessClient readinessClient) {
        return args -> {
            ReadinessResponse readiness = readinessClient.getReadiness();
            log.info(new YAMLMapper().writeValueAsString(ImmutableMap.of("readiness", readiness)));
        };
    }
}
