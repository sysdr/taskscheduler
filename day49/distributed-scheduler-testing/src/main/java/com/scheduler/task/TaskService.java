package com.scheduler.task;

import com.scheduler.lock.DistributedLock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;

@Service
public class TaskService {
    private final TaskRepository repository;
    private final DistributedLock lock;
    private final String instanceId;

    public TaskService(TaskRepository repository, DistributedLock lock,
                      @Value("${scheduler.instance.id}") String instanceId) {
        this.repository = repository;
        this.lock = lock;
        this.instanceId = instanceId;
    }

    @Transactional
    public Task createTask(String name, String description) {
        return repository.save(new Task(name, description));
    }

    @Transactional
    public boolean executeTask(Long taskId) {
        String lockKey = "task:lock:" + taskId;
        if (!lock.tryLock(lockKey, instanceId, Duration.ofSeconds(30))) return false;

        try {
            Task task = repository.findById(taskId).orElse(null);
            if (task == null || task.getStatus() != TaskStatus.PENDING) return false;

            task.setStatus(TaskStatus.COMPLETED);
            task.setExecutedBy(instanceId);
            task.setExecutionCount(task.getExecutionCount() + 1);
            repository.save(task);
            return true;
        } finally {
            lock.unlock(lockKey, instanceId);
        }
    }

    public List<Task> getAllTasks() {
        return repository.findAll();
    }

    public List<Task> getPendingTasks() {
        return repository.findByStatus(TaskStatus.PENDING);
    }

    public long getTasksExecutedBy(String instanceId) {
        return repository.countByExecutedBy(instanceId);
    }
}
