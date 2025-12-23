package com.taskscheduler.model;

public enum OrderStatus {
    CREATED,
    VALIDATING,
    VALIDATED,
    CHARGING,
    CHARGED,
    FULFILLING,
    FULFILLED,
    NOTIFYING,
    COMPLETED,
    FAILED,
    COMPENSATING,
    COMPENSATED
}
