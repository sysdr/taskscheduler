package com.taskscheduler.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class UserActionEvent extends BaseEvent {
    private String userId;
    private String username;
    private String actionType; // REGISTRATION, LOGIN, PROFILE_UPDATE, etc.
    private String email;

    public UserActionEvent() {
        setEventType("USER_ACTION");
        setSource("USER_SERVICE");
    }
}
