package com.epages.readiness.sort;

import org.springframework.boot.actuate.health.Status;

import java.util.Comparator;
import java.util.List;

import static org.springframework.boot.actuate.health.Status.DOWN;
import static org.springframework.boot.actuate.health.Status.OUT_OF_SERVICE;
import static org.springframework.boot.actuate.health.Status.UNKNOWN;
import static org.springframework.boot.actuate.health.Status.UP;

public class StatusComparator implements Comparator<Status> {

    private final List<Status> statusOrder = List.of(DOWN, OUT_OF_SERVICE, UNKNOWN, UP);

    @Override
    public int compare(Status first, Status second) {
        int firstIndex = this.statusOrder.indexOf(first);
        int secondIndex = this.statusOrder.indexOf(second);
        if (firstIndex == secondIndex)
            return first.getCode().compareTo(second.getCode());

        return firstIndex < secondIndex ? -1 : 1;
    }
}
