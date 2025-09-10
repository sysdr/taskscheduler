package com.taskscheduler.repository;

import com.taskscheduler.model.LeaderElection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface LeaderElectionRepository extends JpaRepository<LeaderElection, String> {
    
    @Query("SELECT le FROM LeaderElection le WHERE le.serviceName = :serviceName")
    Optional<LeaderElection> findByServiceName(@Param("serviceName") String serviceName);
    
    @Modifying
    @Transactional
    @Query(value = """
        INSERT INTO leader_election (service_name, leader_instance_id, lease_expires_at, heartbeat_interval_ms, created_at, updated_at)
        VALUES (:serviceName, :instanceId, :expiresAt, :heartbeatInterval, :now, :now)
        ON DUPLICATE KEY UPDATE
            leader_instance_id = CASE 
                WHEN lease_expires_at < :now THEN :instanceId
                ELSE leader_instance_id
            END,
            lease_expires_at = CASE 
                WHEN lease_expires_at < :now OR leader_instance_id = :instanceId 
                THEN :expiresAt
                ELSE lease_expires_at
            END,
            updated_at = :now
        """, nativeQuery = true)
    int tryAcquireOrRenewLease(@Param("serviceName") String serviceName,
                              @Param("instanceId") String instanceId,
                              @Param("expiresAt") LocalDateTime expiresAt,
                              @Param("heartbeatInterval") Integer heartbeatInterval,
                              @Param("now") LocalDateTime now);
    
    @Modifying
    @Transactional
    @Query("DELETE FROM LeaderElection le WHERE le.serviceName = :serviceName AND le.leaderInstanceId = :instanceId")
    int releaseLease(@Param("serviceName") String serviceName, @Param("instanceId") String instanceId);
}
