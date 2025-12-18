package com.scheduler.filter;

import com.scheduler.config.TenantContext;
import com.scheduler.model.Tenant;
import com.scheduler.repository.TenantRepository;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class TenantFilter implements Filter {
    
    private final TenantRepository tenantRepository;
    
    public TenantFilter(TenantRepository tenantRepository) {
        this.tenantRepository = tenantRepository;
    }
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        // Extract tenant from header or API key
        String tenantId = httpRequest.getHeader("X-Tenant-ID");
        String apiKey = httpRequest.getHeader("X-API-Key");
        
        if (apiKey != null && !apiKey.isEmpty()) {
            tenantRepository.findByApiKey(apiKey).ifPresent(tenant -> {
                TenantContext.setCurrentTenant(tenant.getTenantId());
            });
        } else if (tenantId != null && !tenantId.isEmpty()) {
            TenantContext.setCurrentTenant(tenantId);
        }
        
        try {
            chain.doFilter(request, response);
        } finally {
            TenantContext.clear();
        }
    }
}
