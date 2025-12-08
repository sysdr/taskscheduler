package com.taskscheduler.service;

import com.taskscheduler.model.AuditLog;
import com.taskscheduler.repository.AuditLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AuditService {
    
    @Autowired
    private AuditLogRepository auditLogRepository;
    
    public void logAction(String username, String action, String resourceType,
                          Long resourceId, String details) {
        AuditLog log = new AuditLog();
        log.setUsername(username);
        log.setAction(action);
        log.setResourceType(resourceType);
        log.setResourceId(resourceId);
        log.setDetails(details);
        log.setTimestamp(LocalDateTime.now());
        log.setResult(AuditLog.ActionResult.SUCCESS);
        
        auditLogRepository.save(log);
    }
    
    public List<AuditLog> getUserAuditLogs(String username) {
        return auditLogRepository.findByUsernameOrderByTimestampDesc(username);
    }
    
    public List<AuditLog> getAuditLogsByDateRange(LocalDateTime start, LocalDateTime end) {
        return auditLogRepository.findByTimestampBetween(start, end);
    }
}
