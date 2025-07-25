package com.epages.readiness.sort;

import com.epages.readiness.HealthRequest;
import com.epages.readiness.HealthResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;

import java.util.Comparator;
import java.util.List;

import static org.assertj.core.api.BDDAssertions.then;
import static org.springframework.boot.actuate.health.Status.DOWN;
import static org.springframework.boot.actuate.health.Status.OUT_OF_SERVICE;
import static org.springframework.boot.actuate.health.Status.UNKNOWN;
import static org.springframework.boot.actuate.health.Status.UP;
import static org.springframework.data.domain.Sort.Direction.ASC;
import static org.springframework.data.domain.Sort.Direction.DESC;

class SortCompareTest {

    private SortCompare sortCompare;

    @BeforeEach
    void setup() {
        sortCompare = new SortConfiguration().sortCompare();
    }

    @Test
    void should_sort_health_responses() {
        // GIVEN
        Sort sort = Sort.by(
            new Order(ASC, "status"), new Order(DESC, "totalTimeMillis"), new Order(ASC, "service")
        );

        // WHEN
        Comparator<HealthResponse> comparator = sortCompare.getComparator(sort);
        List<HealthResponse> sorted = givenHealthResponses()
            .stream()
            .sorted(comparator)
            .toList();

        // THEN
        then(sorted).extracting(HealthResponse::getService).containsExactly("E", "D", "C", "B", "A");
    }

    private List<HealthResponse> givenHealthResponses() {
        return List.of(
                HealthResponse.builder().request(new HealthRequest("A", "/A")).status(UP).totalTimeMillis(1L).build(),
                HealthResponse.builder().request(new HealthRequest("B", "/B")).status(UNKNOWN).totalTimeMillis(1L).build(),
                HealthResponse.builder().request(new HealthRequest("C", "/C")).status(UNKNOWN).totalTimeMillis(2L).build(),
                HealthResponse.builder().request(new HealthRequest("D", "/D")).status(OUT_OF_SERVICE).totalTimeMillis(1L).build(),
                HealthResponse.builder().request(new HealthRequest("E", "/E")).status(DOWN).totalTimeMillis(1L).build()
        );
    }
}
