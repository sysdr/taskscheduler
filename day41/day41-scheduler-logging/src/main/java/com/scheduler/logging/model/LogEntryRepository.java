package com.scheduler.logging.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface LogEntryRepository extends JpaRepository<LogEntry, Long> {
    @Query("SELECT l FROM LogEntry l ORDER BY l.timestamp DESC")
    List<LogEntry> findRecentLogs();
    
    List<LogEntry> findByTaskIdOrderByTimestampDesc(String taskId);
    
    List<LogEntry> findByLevelOrderByTimestampDesc(String level);
    
    @Query("SELECT COUNT(l) FROM LogEntry l WHERE l.level = ?1")
    long countByLevel(String level);
}
