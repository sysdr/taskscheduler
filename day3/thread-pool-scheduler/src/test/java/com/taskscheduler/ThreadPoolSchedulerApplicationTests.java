package com.taskscheduler;

import com.taskscheduler.service.ScheduledTasksService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestPropertySource(properties = {
    "task.scheduler.pool-size=5"
})
class ThreadPoolSchedulerApplicationTests {

    @Autowired
    private ThreadPoolTaskScheduler customTaskScheduler;
    
    @Autowired
    private ScheduledTasksService scheduledTasksService;

    @Test
    void contextLoads() {
        assertThat(customTaskScheduler).isNotNull();
        assertThat(scheduledTasksService).isNotNull();
    }
    
    @Test
    void customTaskSchedulerConfiguration() {
        assertThat(customTaskScheduler.getPoolSize()).isEqualTo(5);
        assertThat(customTaskScheduler.getThreadNamePrefix()).isEqualTo("custom-scheduler-");
    }
}
