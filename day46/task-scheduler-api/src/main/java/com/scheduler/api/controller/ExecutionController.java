package com.scheduler.api.controller;

import com.scheduler.api.dto.ApiResponse;
import com.scheduler.api.dto.ExecutionResponse;
import com.scheduler.api.model.ExecutionStatus;
import com.scheduler.api.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/executions")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ExecutionController {
    
    private final TaskService taskService;
    
    @GetMapping
    public ResponseEntity<Page<ExecutionResponse>> getExecutions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) Long taskId,
            @RequestParam(required = false) ExecutionStatus status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return ResponseEntity.ok(taskService.getExecutions(page, size, taskId, status, startDate, endDate));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ExecutionResponse>> getExecutionById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(taskService.getExecutionById(id)));
    }
}
