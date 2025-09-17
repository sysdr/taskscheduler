package com.taskscheduler.controller;

import com.taskscheduler.model.ExecutionStatus;
import com.taskscheduler.model.TaskExecution;
import com.taskscheduler.repository.TaskExecutionRepository;
import com.taskscheduler.service.EmailNotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class DashboardController {
    
    @Autowired
    private TaskExecutionRepository repository;
    
    @Autowired
    private EmailNotificationService emailService;

    @GetMapping("/")
    public String dashboard(Model model, 
                          @RequestParam(defaultValue = "0") int page,
                          @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<TaskExecution> executions = repository.findAll(pageable);
        
        // Get execution statistics
        long totalExecutions = repository.count();
        long completedExecutions = repository.countByStatus(ExecutionStatus.COMPLETED);
        long failedExecutions = repository.countByStatus(ExecutionStatus.FAILED);
        long runningExecutions = repository.countByStatus(ExecutionStatus.RUNNING);
        
        model.addAttribute("executions", executions);
        model.addAttribute("totalExecutions", totalExecutions);
        model.addAttribute("completedExecutions", completedExecutions);
        model.addAttribute("failedExecutions", failedExecutions);
        model.addAttribute("runningExecutions", runningExecutions);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", executions.getTotalPages());
        
        return "dashboard";
    }

    @GetMapping("/api/stats")
    @ResponseBody
    public Map<String, Object> getStats() {
        return Map.of(
            "total", repository.count(),
            "completed", repository.countByStatus(ExecutionStatus.COMPLETED),
            "failed", repository.countByStatus(ExecutionStatus.FAILED),
            "running", repository.countByStatus(ExecutionStatus.RUNNING),
            "pending", repository.countByStatus(ExecutionStatus.PENDING)
        );
    }

    @PostMapping("/api/test-email")
    @ResponseBody
    public ResponseEntity<String> testEmail(@RequestParam String email, @RequestParam String name) {
        try {
            String result = emailService.sendWelcomeEmail(email, name);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to send email: " + e.getMessage());
        }
    }

    @PostMapping("/api/test-reset")
    @ResponseBody
    public ResponseEntity<String> testPasswordReset(@RequestParam String email) {
        try {
            String token = "reset_" + System.currentTimeMillis();
            String result = emailService.sendPasswordResetEmail(email, token);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to send reset email: " + e.getMessage());
        }
    }

    @GetMapping("/api/executions")
    @ResponseBody
    public List<TaskExecution> getRecentExecutions(@RequestParam(defaultValue = "20") int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return repository.findAll(pageable).getContent();
    }
}
