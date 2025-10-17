package com.scheduler.controller;

import com.scheduler.model.TaskStatus;
import com.scheduler.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {
    
    @Autowired
    private TaskService taskService;
    
    @GetMapping("/")
    public String dashboard(Model model) {
        model.addAttribute("totalTasks", taskService.getAllTasks().size());
        model.addAttribute("pendingTasks", taskService.getTaskCountByStatus(TaskStatus.PENDING));
        model.addAttribute("executingTasks", taskService.getTaskCountByStatus(TaskStatus.EXECUTING));
        model.addAttribute("completedTasks", taskService.getTaskCountByStatus(TaskStatus.COMPLETED));
        model.addAttribute("failedTasks", taskService.getTaskCountByStatus(TaskStatus.FAILED));
        return "dashboard";
    }
}
