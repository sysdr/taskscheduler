package com.scheduler.service;

import com.scheduler.model.Tenant;
import com.scheduler.model.TenantMetrics;
import com.scheduler.repository.TenantRepository;
import com.scheduler.repository.TenantMetricsRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
public class ResourceGovernor {
    
    private final TenantRepository tenantRepository;
    private final TenantMetricsRepository metricsRepository;
    
    public ResourceGovernor(TenantRepository tenantRepository, TenantMetricsRepository metricsRepository) {
        this.tenantRepository = tenantRepository;
        this.metricsRepository = metricsRepository;
    }
    
    @Transactional
    public boolean canExecuteTask(String tenantId) {
        Tenant tenant = tenantRepository.findByTenantId(tenantId)
            .orElseThrow(() -> new RuntimeException("Tenant not found: " + tenantId));
        
        if (!tenant.getActive()) {
            return false;
        }
        
        TenantMetrics metrics = metricsRepository.findByTenantId(tenantId)
            .orElseGet(() -> {
                TenantMetrics m = new TenantMetrics();
                m.setTenantId(tenantId);
                return metricsRepository.save(m);
            });
        
        // Reset daily counter if needed
        if (ChronoUnit.DAYS.between(metrics.getLastResetTime(), LocalDateTime.now()) >= 1) {
            metrics.setTasksToday(0);
            metrics.setLastResetTime(LocalDateTime.now());
            metricsRepository.save(metrics);
        }
        
        // Check concurrent tasks limit
        if (metrics.getCurrentRunningTasks() >= tenant.getMaxConcurrentTasks()) {
            return false;
        }
        
        // Check daily tasks limit
        if (metrics.getTasksToday() >= tenant.getMaxTasksPerDay()) {
            return false;
        }
        
        return true;
    }
    
    @Transactional
    public void incrementRunningTasks(String tenantId) {
        TenantMetrics metrics = metricsRepository.findByTenantId(tenantId)
            .orElseThrow(() -> new RuntimeException("Metrics not found"));
        
        metrics.setCurrentRunningTasks(metrics.getCurrentRunningTasks() + 1);
        metrics.setTasksToday(metrics.getTasksToday() + 1);
        metricsRepository.save(metrics);
    }
    
    @Transactional
    public void decrementRunningTasks(String tenantId) {
        TenantMetrics metrics = metricsRepository.findByTenantId(tenantId)
            .orElseThrow(() -> new RuntimeException("Metrics not found"));
        
        metrics.setCurrentRunningTasks(Math.max(0, metrics.getCurrentRunningTasks() - 1));
        metricsRepository.save(metrics);
    }
    
    public TenantMetrics getMetrics(String tenantId) {
        return metricsRepository.findByTenantId(tenantId)
            .orElseGet(() -> {
                TenantMetrics m = new TenantMetrics();
                m.setTenantId(tenantId);
                return m;
            });
    }
}
