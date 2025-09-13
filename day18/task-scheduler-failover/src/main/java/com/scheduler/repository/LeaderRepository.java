package com.scheduler.repository;

import com.scheduler.model.Leader;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import jakarta.persistence.LockModeType;
import java.util.Optional;

@Repository
public interface LeaderRepository extends JpaRepository<Leader, String> {
    
    @Query("SELECT l FROM Leader l WHERE l.id = 'SINGLETON_LEADER'")
    Optional<Leader> findCurrentLeader();
    
    @Query("SELECT l FROM Leader l WHERE l.id = 'SINGLETON_LEADER' AND l.nodeId = ?1")
    Optional<Leader> findByNodeId(String nodeId);
}
