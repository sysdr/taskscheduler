package com.example.distributedlock;

import com.example.distributedlock.model.TaskExecutionLogRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class DistributedLockDemoApplicationTests {

    @Autowired
    private TaskExecutionLogRepository repository;

    @Test
    void contextLoads() {
        assertThat(repository).isNotNull();
    }
}
