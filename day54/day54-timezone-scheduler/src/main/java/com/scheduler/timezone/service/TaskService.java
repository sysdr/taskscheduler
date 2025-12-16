package com.scheduler.timezone.service;

import com.scheduler.timezone.dto.ExecutionHistoryResponse;
import com.scheduler.timezone.dto.TaskRequest;
import com.scheduler.timezone.dto.TaskResponse;
import com.scheduler.timezone.model.Task;
import com.scheduler.timezone.model.TaskExecution;
import com.scheduler.timezone.model.TaskStatus;
import com.scheduler.timezone.repository.TaskExecutionRepository;
import com.scheduler.timezone.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskService {
    
    private final TaskRepository taskRepository;
    private final TaskExecutionRepository executionRepository;
    private final TimeZoneService timeZoneService;
    
    @Transactional
    public TaskResponse createTask(TaskRequest request) {
        ZoneId zoneId = ZoneId.of(request.getTimeZone());
        
        // Parse scheduled time (HH:mm format)
        String[] timeParts = request.getScheduledTime().split(":");
        int hour = Integer.parseInt(timeParts[0]);
        int minute = Integer.parseInt(timeParts[1]);
        
        // Create cron expression: "minute hour * * *" (daily)
        String cronExpression = String.format("%d %d * * *", minute, hour);
        
        ZonedDateTime now = ZonedDateTime.now(zoneId);
        ZonedDateTime nextRun = timeZoneService.calculateNextExecution(
                cronExpression, zoneId, now);
        
        Task task = Task.builder()
                .name(request.getName())
                .description(request.getDescription())
                .cronExpression(cronExpression)
                .timeZone(request.getTimeZone())
                .nextRunUtc(nextRun.toInstant())
                .originalScheduledTime(nextRun.format(DateTimeFormatter.ISO_ZONED_DATE_TIME))
                .status(TaskStatus.ACTIVE)
                .build();
        
        task = taskRepository.save(task);
        log.info("Created task: {} scheduled for {}", task.getName(), nextRun);
        
        return toResponse(task);
    }
    
    public List<TaskResponse> getAllTasks() {
        return taskRepository.findByStatusOrderByNextRunUtcAsc(TaskStatus.ACTIVE)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
    
    public TaskResponse getTask(String id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        return toResponse(task);
    }
    
    @Transactional
    public void deleteTask(String id) {
        taskRepository.deleteById(id);
        log.info("Deleted task: {}", id);
    }
    
    public List<ExecutionHistoryResponse> getExecutionHistory(String taskId) {
        return executionRepository.findByTaskIdOrderByExecutionTimeUtcDesc(taskId)
                .stream()
                .map(this::toExecutionResponse)
                .collect(Collectors.toList());
    }
    
    public List<ExecutionHistoryResponse> getRecentExecutions() {
        return executionRepository.findTop100ByOrderByExecutionTimeUtcDesc()
                .stream()
                .map(this::toExecutionResponse)
                .collect(Collectors.toList());
    }
    
    public long getTotalExecutionCount() {
        return executionRepository.count();
    }
    
    private TaskResponse toResponse(Task task) {
        ZoneId zoneId = ZoneId.of(task.getTimeZone());
        ZonedDateTime nextRunLocal = task.getNextRunUtc().atZone(zoneId);
        
        return TaskResponse.builder()
                .id(task.getId())
                .name(task.getName())
                .description(task.getDescription())
                .cronExpression(task.getCronExpression())
                .timeZone(task.getTimeZone())
                .nextRunUtc(task.getNextRunUtc())
                .nextRunLocal(nextRunLocal.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .lastExecutionUtc(task.getLastExecutionUtc())
                .executionCount(task.getExecutionCount())
                .status(task.getStatus().name())
                .dstActive(timeZoneService.isDSTActive(zoneId, task.getNextRunUtc()))
                .utcOffset(timeZoneService.getUTCOffset(zoneId, task.getNextRunUtc()))
                .dstWarning(timeZoneService.getDSTWarning(zoneId, task.getNextRunUtc()).orElse(null))
                .build();
    }
    
    private ExecutionHistoryResponse toExecutionResponse(TaskExecution execution) {
        Task task = taskRepository.findById(execution.getTaskId()).orElse(null);
        
        return ExecutionHistoryResponse.builder()
                .id(execution.getId())
                .taskId(execution.getTaskId())
                .taskName(task != null ? task.getName() : "Unknown")
                .executionTimeUtc(execution.getExecutionTimeUtc())
                .executionTimeLocal(execution.getExecutionTimeLocal())
                .timeZone(execution.getTimeZone())
                .dstInEffect(execution.getDstInEffect())
                .utcOffset(execution.getUtcOffset())
                .executionStatus(execution.getExecutionStatus())
                .durationMs(execution.getDurationMs())
                .notes(execution.getNotes())
                .build();
    }
}
