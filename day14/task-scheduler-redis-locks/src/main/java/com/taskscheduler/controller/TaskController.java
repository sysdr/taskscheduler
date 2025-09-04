package com.taskscheduler.controller;

import com.taskscheduler.model.Task;
import com.taskscheduler.service.TaskService;
import com.taskscheduler.service.TaskExecutorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import java.util.List;
import java.util.Map;

@Controller
public class TaskController {
    
    @Autowired
    private TaskService taskService;
    
    @Autowired
    private TaskExecutorService executorService;
    
    @GetMapping("/")
    public String dashboard(Model model) {
        model.addAttribute("stats", taskService.getSystemStats());
        model.addAttribute("executorId", executorService.getExecutorId());
        return "dashboard";
    }
    
    @PostMapping("/api/tasks")
    @ResponseBody
    public Task createTask(@RequestParam String name) {
        return taskService.createTask(name);
    }
    
    @GetMapping("/api/tasks")
    @ResponseBody
    public List<Task> getAllTasks() {
        return taskService.getAllTasks();
    }
    
    @GetMapping("/api/stats")
    @ResponseBody
    public Map<String, Object> getStats() {
        return taskService.getSystemStats();
    }
    
    @GetMapping("/api/running-tasks")
    @ResponseBody
    public List<Task> getRunningTasks() {
        return taskService.getRunningTasks();
    }
}
