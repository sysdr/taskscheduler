package com.taskscheduler;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
    "spring.redis.host=localhost",
    "spring.redis.port=6370"  // Non-standard port for tests
})
class CoordinationApplicationTests {

    @Test
    void contextLoads() {
        // Context loading test
    }
}
