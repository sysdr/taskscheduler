package com.taskscheduler.repository;

import com.taskscheduler.entity.TaskLock;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class TaskLockRepositoryTest {
    
    @Autowired
    private TaskLockRepository lockRepository;
    
    @Test
    void shouldSaveAndFindLock() {
        TaskLock lock = new TaskLock("test-lock", "instance-1", 
            LocalDateTime.now().plusMinutes(5));
        
        TaskLock saved = lockRepository.save(lock);
        assertNotNull(saved.getId());
        
        Optional<TaskLock> found = lockRepository.findByLockKey("test-lock");
        assertTrue(found.isPresent());
        assertEquals("test-lock", found.get().getLockKey());
    }
    
    @Test
    void shouldFindExpiredLocks() {
        LocalDateTime now = LocalDateTime.now();
        
        // Create expired lock
        TaskLock expiredLock = new TaskLock("expired-lock", "instance-1", 
            now.minusMinutes(1));
        lockRepository.save(expiredLock);
        
        // Create active lock
        TaskLock activeLock = new TaskLock("active-lock", "instance-2", 
            now.plusMinutes(1));
        lockRepository.save(activeLock);
        
        List<TaskLock> expiredLocks = lockRepository.findExpiredLocks(now);
        
        assertEquals(1, expiredLocks.size());
        assertEquals("expired-lock", expiredLocks.get(0).getLockKey());
    }
    
    @Test
    void shouldDeleteByLockKeyAndOwner() {
        TaskLock lock = new TaskLock("delete-test", "instance-1", 
            LocalDateTime.now().plusMinutes(5));
        lockRepository.save(lock);
        
        int deleted = lockRepository.deleteByLockKeyAndOwner("delete-test", "instance-1");
        assertEquals(1, deleted);
        
        assertFalse(lockRepository.findByLockKey("delete-test").isPresent());
    }
    
    @Test
    void shouldCountActiveLocks() {
        LocalDateTime now = LocalDateTime.now();
        
        // Create mix of active and expired locks
        lockRepository.save(new TaskLock("active-1", "instance-1", now.plusMinutes(5)));
        lockRepository.save(new TaskLock("active-2", "instance-2", now.plusMinutes(10)));
        lockRepository.save(new TaskLock("expired-1", "instance-3", now.minusMinutes(5)));
        
        long activeCount = lockRepository.countActiveLocks(now);
        assertEquals(2, activeCount);
    }
}
