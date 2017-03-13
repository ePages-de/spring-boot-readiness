package com.epages.readiness.sort;

import com.epages.readiness.HealthResponse;

import org.springframework.data.domain.Sort.Order;

import java.util.Comparator;
import java.util.Map;
import java.util.function.Function;

import lombok.RequiredArgsConstructor;

import static com.epages.readiness.sort.SortConfiguration.SORT_BY_SERVICE;

@RequiredArgsConstructor
public class OrderCompare implements Function<Order, Comparator<HealthResponse>> {

    private final Map<String, Comparator<HealthResponse>> comparators;

    @Override
    public Comparator<HealthResponse> apply(Order order) {
        Comparator<HealthResponse> comparator = comparators.getOrDefault(order.getProperty(), SORT_BY_SERVICE);
        return order.getDirection().isDescending() ? comparator.reversed() : comparator;
    }
}
