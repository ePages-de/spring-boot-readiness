package com.epages.readiness.sort;

import com.epages.readiness.HealthResponse;

import org.springframework.data.domain.Sort;
import org.springframework.util.comparator.CompoundComparator;

import java.util.Comparator;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.stream.StreamSupport.stream;

@RequiredArgsConstructor
public class SortCompare {

    private final OrderCompare orderCompare;

    public Comparator<HealthResponse> getComparator(@NonNull Sort sort) {
        return stream(checkNotNull(sort).spliterator(), false)
                .map(orderCompare)
                .reduce(Comparator::thenComparing)
                .orElse((t1, t2) -> 0);
    }
}
