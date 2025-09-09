package com.scheduler.leader.repository;

import com.scheduler.leader.model.Leadership;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface LeadershipRepository extends JpaRepository<Leadership, String> {
    
    @Query("SELECT l FROM Leadership l WHERE l.id = 'SCHEDULER_LEADER'")
    Optional<Leadership> findCurrentLeadership();
    
    @Modifying
    @Query("UPDATE Leadership l SET l.leaderId = :leaderId, l.leaseStart = :start, l.leaseEnd = :end, l.updatedAt = :now WHERE l.id = 'SCHEDULER_LEADER' AND l.leaseEnd < :now")
    int attemptLeadershipTakeover(@Param("leaderId") String leaderId, 
                                  @Param("start") LocalDateTime start, 
                                  @Param("end") LocalDateTime end, 
                                  @Param("now") LocalDateTime now);
    
    @Modifying
    @Query("UPDATE Leadership l SET l.leaseEnd = :newEnd, l.updatedAt = :now WHERE l.id = 'SCHEDULER_LEADER' AND l.leaderId = :leaderId AND l.leaseEnd > :now")
    int renewLease(@Param("leaderId") String leaderId, 
                   @Param("newEnd") LocalDateTime newEnd, 
                   @Param("now") LocalDateTime now);
}
