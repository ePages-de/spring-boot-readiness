package com.epages.readiness.sort;

import com.epages.readiness.HealthResponse;

import org.springframework.data.domain.Sort;
import org.springframework.util.comparator.CompoundComparator;

import java.util.Comparator;

import javax.validation.constraints.NotNull;

import lombok.RequiredArgsConstructor;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.stream.StreamSupport.stream;

@RequiredArgsConstructor
public class SortCompare {

    private final OrderCompare orderCompare;

    public Comparator<HealthResponse> getComparator(@NotNull Sort sort) {
        return stream(checkNotNull(sort).spliterator(), false)
                .map(orderCompare)
                .collect(CompoundComparator::new, CompoundComparator::addComparator, CompoundComparator::addComparator);
    }
}
