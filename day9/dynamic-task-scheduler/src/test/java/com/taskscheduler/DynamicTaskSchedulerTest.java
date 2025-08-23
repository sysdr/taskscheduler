package com.taskscheduler;

import com.taskscheduler.entity.TaskDefinition;
import com.taskscheduler.repository.TaskDefinitionRepository;
import com.taskscheduler.service.DynamicTaskScheduler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
public class DynamicTaskSchedulerTest {

    @Autowired
    private TaskDefinitionRepository taskRepository;

    @Autowired
    private DynamicTaskScheduler dynamicScheduler;

    @Test
    public void contextLoads() {
        assertThat(taskRepository).isNotNull();
        assertThat(dynamicScheduler).isNotNull();
    }

    @Test
    public void testTaskCreationAndScheduling() {
        // Create a test task
        TaskDefinition task = new TaskDefinition(
            "test-task",
            "Test task description",
            "0 */10 * * * *", // Every 10 minutes
            TaskDefinition.TaskType.LOG_MESSAGE
        );
        task.setStatus(TaskDefinition.TaskStatus.ACTIVE);

        // Save to repository
        TaskDefinition savedTask = taskRepository.save(task);
        assertThat(savedTask.getId()).isNotNull();

        // Schedule the task
        dynamicScheduler.scheduleTask(savedTask);

        // Verify task is scheduled
        assertThat(dynamicScheduler.isTaskScheduled(savedTask.getId())).isTrue();

        // Cancel the task
        dynamicScheduler.cancelTask(savedTask.getId());
        assertThat(dynamicScheduler.isTaskScheduled(savedTask.getId())).isFalse();
    }
}
