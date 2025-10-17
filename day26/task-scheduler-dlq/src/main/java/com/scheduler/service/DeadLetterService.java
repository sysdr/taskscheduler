package com.scheduler.service;

import com.scheduler.model.DeadLetterTask;
import com.scheduler.model.FailureReason;
import com.scheduler.model.Task;
import com.scheduler.model.TaskStatus;
import com.scheduler.repository.DeadLetterTaskRepository;
import com.scheduler.repository.TaskRepository;
import io.micrometer.core.instrument.MeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
public class DeadLetterService {
    
    private static final Logger logger = LoggerFactory.getLogger(DeadLetterService.class);
    
    @Autowired
    private DeadLetterTaskRepository deadLetterRepository;
    
    @Autowired
    private TaskRepository taskRepository;
    
    @Autowired
    private MeterRegistry meterRegistry;
    
    
    public void moveToDeadLetter(Task task, Exception finalException) {
        logger.warn("Moving task {} to dead letter queue after {} failed attempts", 
                   task.getId(), task.getRetryCount());
        
        // Create stack trace string
        String stackTrace = getStackTrace(finalException);
        
        // Create dead letter task
        DeadLetterTask deadLetterTask = new DeadLetterTask(task, finalException.getMessage(), stackTrace);
        
        // Save to dead letter queue
        deadLetterRepository.save(deadLetterTask);
        
        // Update original task status
        task.setStatus(TaskStatus.DEAD_LETTER);
        task.setLastError(finalException.getMessage());
        taskRepository.save(task);
        
        // Update metrics
        meterRegistry.counter("dlq.tasks.created", 
                            "failure_reason", deadLetterTask.getFailureReason().name()).increment();
        
        logger.info("Task {} moved to DLQ with failure reason: {}", 
                   task.getId(), deadLetterTask.getFailureReason());
    }
    
    public Page<DeadLetterTask> getUnprocessedTasks(Pageable pageable) {
        return deadLetterRepository.findByReprocessedFalseOrderByDeadLetteredAtDesc(pageable);
    }
    
    public List<DeadLetterTask> getTasksByFailureReason(FailureReason reason) {
        return deadLetterRepository.findByFailureReason(reason);
    }
    
    public Optional<DeadLetterTask> getById(String id) {
        return deadLetterRepository.findById(id);
    }
    
    public void reprocessTask(String deadLetterTaskId, String notes) throws Exception {
        DeadLetterTask deadLetterTask = deadLetterRepository.findById(deadLetterTaskId)
            .orElseThrow(() -> new IllegalArgumentException("Dead letter task not found: " + deadLetterTaskId));
        
        if (deadLetterTask.isReprocessed()) {
            throw new IllegalStateException("Task has already been reprocessed");
        }
        
        logger.info("Reprocessing dead letter task: {}", deadLetterTaskId);
        
        // Create new task from dead letter task
        Task newTask = new Task(deadLetterTask.getTaskName(), deadLetterTask.getTaskPayload());
        newTask.setMaxRetries(3); // Reset retry count
        
        // Save new task directly using repository
        Task savedTask = taskRepository.save(newTask);
        
        // Mark dead letter task as reprocessed
        deadLetterTask.setNotes(notes);
        deadLetterTask.markAsReprocessed();
        deadLetterRepository.save(deadLetterTask);
        
        // Update metrics
        meterRegistry.counter("dlq.tasks.reprocessed").increment();
        
        logger.info("Successfully reprocessed dead letter task {} as new task {}", 
                   deadLetterTaskId, savedTask.getId());
    }
    
    public Map<String, Long> getFailureReasonStats() {
        List<Object[]> stats = deadLetterRepository.getFailureReasonStats();
        Map<String, Long> result = new HashMap<>();
        
        for (Object[] stat : stats) {
            FailureReason reason = (FailureReason) stat[0];
            Long count = (Long) stat[1];
            result.put(reason.name(), count);
        }
        
        return result;
    }
    
    public long getUnprocessedTaskCount() {
        return deadLetterRepository.countUnprocessedTasks();
    }
    
    public List<DeadLetterTask> getTasksInTimeRange(LocalDateTime startDate, LocalDateTime endDate) {
        return deadLetterRepository.findTasksInTimeRange(startDate, endDate);
    }
    
    private String getStackTrace(Exception e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        return sw.toString();
    }
}
