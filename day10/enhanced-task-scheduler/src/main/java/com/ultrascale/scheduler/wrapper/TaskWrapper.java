package com.ultrascale.scheduler.wrapper;

import com.ultrascale.scheduler.model.TaskResult;

public interface TaskWrapper {
    
    TaskResult execute();
    
    String getTaskName();
    
    Long getTaskId();
}
