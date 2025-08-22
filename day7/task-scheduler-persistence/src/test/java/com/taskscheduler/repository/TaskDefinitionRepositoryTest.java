package com.taskscheduler.repository;

import com.taskscheduler.entity.TaskDefinition;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("dev")
class TaskDefinitionRepositoryTest {
    
    @Autowired
    private TaskDefinitionRepository repository;
    
    @Test
    void shouldSaveAndFindTaskDefinition() {
        TaskDefinition task = new TaskDefinition("test-task", "TEST", "0 0 * * * ?");
        task.setDescription("Test task description");
        
        TaskDefinition saved = repository.save(task);
        
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo("test-task");
        
        Optional<TaskDefinition> found = repository.findByName("test-task");
        assertThat(found).isPresent();
        assertThat(found.get().getDescription()).isEqualTo("Test task description");
    }
    
    @Test
    void shouldFindByStatus() {
        TaskDefinition activeTask = new TaskDefinition("active-task", "TEST", "0 0 * * * ?");
        activeTask.activate();
        repository.save(activeTask);
        
        var activeTasks = repository.findByStatus(TaskDefinition.TaskStatus.ACTIVE);
        assertThat(activeTasks).hasSize(1);
        assertThat(activeTasks.get(0).getName()).isEqualTo("active-task");
    }
}
