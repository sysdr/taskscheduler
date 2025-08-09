package com.scheduler.controller;

import com.scheduler.model.TaskDefinition;
import com.scheduler.model.TaskExecution;
import com.scheduler.repository.TaskDefinitionRepository;
import com.scheduler.repository.TaskExecutionRepository;
import com.scheduler.service.DistributedSchedulerService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/scheduler")
public class SchedulerController {
    
    private final TaskDefinitionRepository taskDefinitionRepository;
    private final TaskExecutionRepository taskExecutionRepository;
    private final DistributedSchedulerService schedulerService;
    
    public SchedulerController(TaskDefinitionRepository taskDefinitionRepository,
                             TaskExecutionRepository taskExecutionRepository,
                             DistributedSchedulerService schedulerService) {
        this.taskDefinitionRepository = taskDefinitionRepository;
        this.taskExecutionRepository = taskExecutionRepository;
        this.schedulerService = schedulerService;
    }
    
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        List<TaskDefinition> tasks = taskDefinitionRepository.findAll();
        List<TaskExecution> recentExecutions = taskExecutionRepository
            .findRecentExecutions(LocalDateTime.now().minusHours(24));
        
        model.addAttribute("tasks", tasks);
        model.addAttribute("recentExecutions", recentExecutions);
        model.addAttribute("instanceId", schedulerService.getInstanceId());
        model.addAttribute("activeTasks", taskDefinitionRepository.countActiveTasks());
        model.addAttribute("runningTasks", taskDefinitionRepository.countRunningTasks());
        
        return "dashboard";
    }
    
    @GetMapping("/api/tasks")
    @ResponseBody
    public List<TaskDefinition> getAllTasks() {
        return taskDefinitionRepository.findAll();
    }
    
    @GetMapping("/api/executions")
    @ResponseBody
    public List<TaskExecution> getRecentExecutions() {
        return taskExecutionRepository.findRecentExecutions(LocalDateTime.now().minusHours(1));
    }
    
    @GetMapping("/api/stats")
    @ResponseBody
    public SchedulerStats getStats() {
        return new SchedulerStats(
            schedulerService.getInstanceId(),
            taskDefinitionRepository.countActiveTasks(),
            taskDefinitionRepository.countRunningTasks(),
            taskExecutionRepository.countRunningExecutions()
        );
    }
    
    public record SchedulerStats(String instanceId, long activeTasks, long runningTasks, long runningExecutions) {}
}
