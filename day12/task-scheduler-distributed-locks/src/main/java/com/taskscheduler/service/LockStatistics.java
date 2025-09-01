package com.taskscheduler.service;

public record LockStatistics(
    long totalActiveLocks,
    long totalAcquisitions,
    long totalReleases,
    long totalTimeouts,
    long totalFailures,
    double averageHoldTimeMs
) {}
