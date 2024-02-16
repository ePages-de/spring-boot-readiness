package com.epages.readiness;

import org.springframework.util.StopWatch;

abstract class Timer {

    private Timer() {}

    static StopWatch startTiming(String service) {
        StopWatch stopWatch = new StopWatch(service);
        stopWatch.start();
        return stopWatch;
    }

    static long stopTiming(StopWatch stopWatch) {
        if (stopWatch.isRunning()) {
            stopWatch.stop();
        }
        return stopWatch.getTotalTimeMillis();
    }
}
