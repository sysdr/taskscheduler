package com.scheduler.model;

public class RedlockResult {
    private final boolean success;
    private final String lockValue;
    private final long validityTime;
    private final int acquiredInstances;

    public RedlockResult(boolean success, String lockValue, long validityTime, int acquiredInstances) {
        this.success = success;
        this.lockValue = lockValue;
        this.validityTime = validityTime;
        this.acquiredInstances = acquiredInstances;
    }

    public boolean isSuccess() { return success; }
    public String getLockValue() { return lockValue; }
    public long getValidityTime() { return validityTime; }
    public int getAcquiredInstances() { return acquiredInstances; }
}
