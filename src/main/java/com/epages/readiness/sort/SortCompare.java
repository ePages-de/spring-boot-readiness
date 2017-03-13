package com.epages.readiness.sort;

import com.epages.readiness.HealthResponse;

import org.springframework.data.domain.Sort;
import org.springframework.util.comparator.CompoundComparator;

import java.util.Comparator;

import lombok.RequiredArgsConstructor;

import static java.util.stream.StreamSupport.stream;

@RequiredArgsConstructor
public class SortCompare {

    private final OrderCompare orderCompare;

    public Comparator<HealthResponse> getComparator(Sort sort) {
        return stream(sort.spliterator(), false)
                .map(orderCompare)
                .collect(CompoundComparator::new, CompoundComparator::addComparator, CompoundComparator::addComparator);
    }
}
