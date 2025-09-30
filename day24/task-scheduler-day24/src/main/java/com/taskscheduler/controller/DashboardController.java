package com.taskscheduler.controller;

import com.taskscheduler.model.TaskStatus;
import com.taskscheduler.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.HashMap;
import java.util.Map;

@Controller
public class DashboardController {
    
    private final TaskService taskService;
    
    @Autowired
    public DashboardController(TaskService taskService) {
        this.taskService = taskService;
    }
    
    @GetMapping({"/", "/dashboard"})
    public String dashboard(Model model) {
        Map<TaskStatus, Long> taskCounts = new HashMap<>();
        for (TaskStatus status : TaskStatus.values()) {
            taskCounts.put(status, taskService.getTaskCountByStatus(status));
        }
        
        model.addAttribute("taskCounts", taskCounts);
        return "dashboard";
    }
}
