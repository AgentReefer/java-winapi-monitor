package com.monitor.util;

public class Profiler {
    private long startTime;
    private long endTime;

    public void start() {
        startTime = System.nanoTime();
    }

    public void stop() {
        endTime = System.nanoTime();
    }

    public long getDurationMillis() {
        return (endTime - startTime) / 1_000_000;
    }
}
