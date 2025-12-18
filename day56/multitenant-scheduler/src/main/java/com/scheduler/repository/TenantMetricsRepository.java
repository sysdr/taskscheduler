package com.scheduler.repository;

import com.scheduler.model.TenantMetrics;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface TenantMetricsRepository extends JpaRepository<TenantMetrics, Long> {
    Optional<TenantMetrics> findByTenantId(String tenantId);
}
