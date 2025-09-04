package com.taskscheduler.service;

import com.taskscheduler.model.Task;
import com.taskscheduler.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Service
public class TaskService {
    
    @Autowired
    private TaskRepository taskRepository;
    
    @Autowired
    private RedisLockService lockService;
    
    public Task createTask(String name) {
        Task task = new Task(name);
        return taskRepository.save(task);
    }
    
    public List<Task> getAllTasks() {
        return taskRepository.findAllOrderByCreatedAtDesc();
    }
    
    public Map<String, Object> getSystemStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalTasks", taskRepository.count());
        stats.put("pendingTasks", taskRepository.countByStatus("PENDING"));
        stats.put("runningTasks", taskRepository.countByStatus("RUNNING"));
        stats.put("completedTasks", taskRepository.countByStatus("COMPLETED"));
        stats.put("failedTasks", taskRepository.countByStatus("FAILED"));
        return stats;
    }
    
    public List<Task> getRunningTasks() {
        List<Task> runningTasks = taskRepository.findRunningTasks();
        // Add lock information
        for (Task task : runningTasks) {
            String lockOwner = lockService.getLockOwner(task.getId().toString());
            if (lockOwner != null) {
                task.setExecutorId(lockOwner);
            }
        }
        return runningTasks;
    }
}
