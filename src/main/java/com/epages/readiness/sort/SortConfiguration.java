package com.epages.readiness.sort;

import com.epages.readiness.HealthResponse;

import java.util.Map;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.web.SortHandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Comparator;
import java.util.List;

import static java.lang.String.CASE_INSENSITIVE_ORDER;
import static java.util.Comparator.comparing;
import static java.util.Comparator.comparingLong;

@Configuration
public class SortConfiguration implements WebMvcConfigurer {

    public static final Comparator<HealthResponse> SORT_BY_SERVICE = comparing(HealthResponse::getService, CASE_INSENSITIVE_ORDER);

    public static final Comparator<HealthResponse> SORT_BY_STATUS = comparing(HealthResponse::getStatus, new StatusComparator());

    public static final Comparator<HealthResponse> SORT_BY_TOTAL_TIME_MILLIS = comparingLong(HealthResponse::getTotalTimeMillis);

    @Bean
    public OrderCompare orderCompare() {
        return new OrderCompare(Map.of(
                "status", SORT_BY_STATUS,
                "totalTimeMillis", SORT_BY_TOTAL_TIME_MILLIS,
                "service", SORT_BY_SERVICE
        ));
    }

    @Bean
    public SortCompare sortCompare() {
        return new SortCompare(orderCompare());
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(new SortHandlerMethodArgumentResolver());
    }
}
