package com.ultrascale.scheduler.demo;

import com.ultrascale.scheduler.demo.service.TaskExecutionRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb",
    "app.instance.id=TEST-INSTANCE"
})
class ScheduledDuplicationDemoApplicationTests {

    @Autowired
    private TaskExecutionRepository taskExecutionRepository;

    @Test
    void contextLoads() {
        assertThat(taskExecutionRepository).isNotNull();
    }

    @Test
    void repositoryCanSaveAndRetrieveTaskExecutions() {
        // This test will be enhanced as we build more functionality
        assertThat(taskExecutionRepository.findAll()).isNotNull();
    }
}
