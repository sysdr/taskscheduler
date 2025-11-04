package com.taskscheduler.consumer.service;

import com.taskscheduler.consumer.model.Task;
import com.taskscheduler.consumer.model.ProcessingResult;

public interface TaskProcessor {
    ProcessingResult process(Task task);
    boolean canProcess(String taskType);
}
