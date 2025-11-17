package com.scheduler.controller;

import com.scheduler.model.Tenant;
import com.scheduler.repository.TenantRepository;
import com.scheduler.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/auth")
@CrossOrigin(origins = "*")
public class AuthController {
    
    @Autowired
    private TenantRepository tenantRepository;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@RequestBody Map<String, String> request) {
        Tenant tenant = new Tenant();
        tenant.setTenantId(UUID.randomUUID().toString());
        tenant.setTenantName(request.get("tenantName"));
        tenant.setApiKey(UUID.randomUUID().toString());
        tenantRepository.save(tenant);
        
        Map<String, String> response = new HashMap<>();
        response.put("tenantId", tenant.getTenantId());
        response.put("apiKey", tenant.getApiKey());
        
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/token")
    public ResponseEntity<Map<String, String>> getToken(@RequestBody Map<String, String> request) {
        String apiKey = request.get("apiKey");
        Tenant tenant = tenantRepository.findByApiKey(apiKey)
            .orElseThrow(() -> new RuntimeException("Invalid API key"));
        
        String token = jwtUtil.createToken(tenant.getTenantId());
        
        Map<String, String> response = new HashMap<>();
        response.put("token", token);
        response.put("tenantId", tenant.getTenantId());
        
        return ResponseEntity.ok(response);
    }
}
