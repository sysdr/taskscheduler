package com.taskscheduler.controller;

import com.taskscheduler.repository.TaskExecutionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {
    
    @Autowired
    private TaskExecutionRepository taskExecutionRepository;
    
    @GetMapping("/")
    public String dashboard(Model model) {
        model.addAttribute("tasks", taskExecutionRepository.findAll());
        model.addAttribute("totalTasks", taskExecutionRepository.count());
        return "dashboard";
    }
}
