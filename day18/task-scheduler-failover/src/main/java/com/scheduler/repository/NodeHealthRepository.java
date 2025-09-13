package com.scheduler.repository;

import com.scheduler.model.NodeHealth;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NodeHealthRepository extends JpaRepository<NodeHealth, String> {
    
    @Query("SELECT n FROM NodeHealth n WHERE n.lastHeartbeat > ?1")
    List<NodeHealth> findActiveNodes(LocalDateTime since);
    
    @Query("SELECT n FROM NodeHealth n WHERE n.status = 'HEALTHY'")
    List<NodeHealth> findHealthyNodes();
}
