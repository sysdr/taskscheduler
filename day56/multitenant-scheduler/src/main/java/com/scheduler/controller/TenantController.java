package com.scheduler.controller;

import com.scheduler.model.Tenant;
import com.scheduler.repository.TenantRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/tenants")
public class TenantController {
    
    private final TenantRepository tenantRepository;
    
    public TenantController(TenantRepository tenantRepository) {
        this.tenantRepository = tenantRepository;
    }
    
    @GetMapping
    public List<Tenant> getAllTenants() {
        return tenantRepository.findAll();
    }
    
    @PostMapping
    public Tenant createTenant(@RequestBody Tenant tenant) {
        tenant.setTenantId(UUID.randomUUID().toString());
        tenant.setApiKey(UUID.randomUUID().toString());
        return tenantRepository.save(tenant);
    }
    
    @GetMapping("/{tenantId}")
    public ResponseEntity<Tenant> getTenant(@PathVariable String tenantId) {
        return tenantRepository.findByTenantId(tenantId)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
}
