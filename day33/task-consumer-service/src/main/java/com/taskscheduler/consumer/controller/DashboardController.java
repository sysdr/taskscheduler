package com.taskscheduler.consumer.controller;

import com.taskscheduler.consumer.model.Task;
import com.taskscheduler.consumer.repository.TaskRepository;
import com.taskscheduler.consumer.service.TaskConsumer;
import com.taskscheduler.consumer.service.TaskProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class DashboardController {
    
    @Autowired
    private TaskRepository taskRepository;
    
    @Autowired
    private TaskConsumer taskConsumer;
    
    @Autowired
    private TaskProcessor taskProcessor;
    
    @GetMapping("/")
    public String dashboard(Model model) {
        List<Task> recentTasks = taskRepository.findAllOrderByCreatedAtDesc();
        if (recentTasks.size() > 50) {
            recentTasks = recentTasks.subList(0, 50); // Show only last 50 tasks
        }
        
        Map<String, Long> statusCounts = new HashMap<>();
        statusCounts.put("QUEUED", taskRepository.countByStatus(Task.TaskStatus.QUEUED));
        statusCounts.put("PROCESSING", taskRepository.countByStatus(Task.TaskStatus.PROCESSING));
        statusCounts.put("COMPLETED", taskRepository.countByStatus(Task.TaskStatus.COMPLETED));
        statusCounts.put("FAILED", taskRepository.countByStatus(Task.TaskStatus.FAILED));
        statusCounts.put("RETRY", taskRepository.countByStatus(Task.TaskStatus.RETRY));
        
        model.addAttribute("tasks", recentTasks);
        model.addAttribute("statusCounts", statusCounts);
        model.addAttribute("processedCount", taskConsumer.getProcessedCount());
        model.addAttribute("workerId", taskProcessor.getWorkerId());
        model.addAttribute("totalTasks", taskRepository.count());
        
        return "dashboard";
    }
}
