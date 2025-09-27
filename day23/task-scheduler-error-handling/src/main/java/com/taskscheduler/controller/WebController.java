package com.taskscheduler.controller;

import com.taskscheduler.entity.Task;
import com.taskscheduler.enums.TaskStatus;
import com.taskscheduler.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.Map;

@Controller
public class WebController {
    
    @Autowired
    private TaskService taskService;
    
    @GetMapping("/")
    public String dashboard(Model model, 
                           @RequestParam(defaultValue = "0") int page,
                           @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Task> tasks = taskService.getAllTasks(pageable);
        
        // Get task statistics
        Map<String, Long> stats = new HashMap<>();
        for (TaskStatus status : TaskStatus.values()) {
            stats.put(status.name().toLowerCase(), taskService.getTaskCountByStatus(status));
        }
        
        model.addAttribute("tasks", tasks);
        model.addAttribute("stats", stats);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", tasks.getTotalPages());
        model.addAttribute("totalElements", tasks.getTotalElements());
        
        return "dashboard";
    }
}
