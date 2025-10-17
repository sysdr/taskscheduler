package com.scheduler.repository;

import com.scheduler.model.DeadLetterTask;
import com.scheduler.model.FailureReason;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DeadLetterTaskRepository extends JpaRepository<DeadLetterTask, String> {
    
    Page<DeadLetterTask> findByReprocessedFalseOrderByDeadLetteredAtDesc(Pageable pageable);
    
    List<DeadLetterTask> findByFailureReason(FailureReason reason);
    
    @Query("SELECT COUNT(d) FROM DeadLetterTask d WHERE d.reprocessed = false")
    long countUnprocessedTasks();
    
    @Query("SELECT d.failureReason, COUNT(d) FROM DeadLetterTask d WHERE d.reprocessed = false GROUP BY d.failureReason")
    List<Object[]> getFailureReasonStats();
    
    @Query("SELECT d FROM DeadLetterTask d WHERE d.deadLetteredAt BETWEEN :startDate AND :endDate")
    List<DeadLetterTask> findTasksInTimeRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
}
