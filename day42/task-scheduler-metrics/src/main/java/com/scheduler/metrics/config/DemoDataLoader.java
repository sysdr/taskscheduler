package com.scheduler.metrics.config;

import com.scheduler.metrics.model.Task;
import com.scheduler.metrics.model.TaskStatus;
import com.scheduler.metrics.repository.TaskRepository;
import com.scheduler.metrics.service.TaskExecutorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component
public class DemoDataLoader implements ApplicationRunner {

    private static final Logger logger = LoggerFactory.getLogger(DemoDataLoader.class);
    private static final List<String> TASK_TYPES = List.of("email", "report", "notification", "export");
    private static final List<String> PRIORITIES = List.of("HIGH", "MEDIUM", "LOW");

    private final TaskRepository taskRepository;
    private final TaskExecutorService taskExecutorService;
    private final Random random = new Random();

    private final boolean demoDataEnabled;
    private final int demoTaskCount;
    private final Duration settleTimeout;

    public DemoDataLoader(TaskRepository taskRepository,
                          TaskExecutorService taskExecutorService,
                          @Value("${app.demo-data.enabled:true}") boolean demoDataEnabled,
                          @Value("${app.demo-data.task-count:25}") int demoTaskCount,
                          @Value("${app.demo-data.settle-timeout-ms:4000}") long settleTimeoutMs) {
        this.taskRepository = taskRepository;
        this.taskExecutorService = taskExecutorService;
        this.demoDataEnabled = demoDataEnabled;
        this.demoTaskCount = demoTaskCount;
        this.settleTimeout = Duration.ofMillis(settleTimeoutMs);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (!demoDataEnabled) {
            logger.info("Demo data generation disabled via configuration");
            return;
        }

        if (taskRepository.count() > 0) {
            logger.info("Existing tasks detected; skipping demo data seeding");
            return;
        }

        logger.info("Seeding {} demo tasks for dashboard preview...", demoTaskCount);
        List<Task> seededTasks = new ArrayList<>();

        for (int i = 0; i < demoTaskCount; i++) {
            Task task = taskExecutorService.submitTask(
                    "Demo-Task-" + (i + 1),
                    TASK_TYPES.get(i % TASK_TYPES.size()),
                    PRIORITIES.get(random.nextInt(PRIORITIES.size()))
            );
            seededTasks.add(task);
            Thread.sleep(25); // stagger submissions for more varied metrics
        }

        waitForCompletion();
        logger.info("Demo data ready: {} tasks persisted", seededTasks.size());
    }

    private void waitForCompletion() throws InterruptedException {
        long deadline = System.currentTimeMillis() + settleTimeout.toMillis();
        while (System.currentTimeMillis() < deadline) {
            long pending = taskRepository.countByStatus(TaskStatus.QUEUED)
                    + taskRepository.countByStatus(TaskStatus.EXECUTING);
            if (pending == 0) {
                return;
            }
            Thread.sleep(100);
        }
        logger.warn("Demo tasks still processing after {} ms; dashboard metrics may take longer to stabilize",
                settleTimeout.toMillis());
    }
}

