package com.taskscheduler.security;

import com.taskscheduler.service.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {
    
    @Autowired
    private CustomUserDetailsService userDetailsService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        
        String requestPath = request.getRequestURI();
        
        // Skip JWT filter for static resources and public endpoints
        if (requestPath.startsWith("/css/") || 
            requestPath.startsWith("/js/") || 
            requestPath.startsWith("/images/") ||
            requestPath.equals("/favicon.ico") ||
            requestPath.endsWith(".ico")) {
            chain.doFilter(request, response);
            return;
        }
        
        final String authorizationHeader = request.getHeader("Authorization");
        
        String username = null;
        String jwt = null;
        
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
            try {
                username = jwtUtil.extractUsername(jwt);
            } catch (Exception e) {
                logger.error("JWT token extraction failed", e);
            }
        }
        
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
                
                if (jwtUtil.validateToken(jwt, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails, null, userDetails.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    logger.debug("JWT token validated successfully for user: " + username);
                } else {
                    logger.warn("JWT token validation failed for user: " + username + " - token invalid or expired");
                }
            } catch (org.springframework.security.core.userdetails.UsernameNotFoundException e) {
                logger.error("User not found during JWT validation: " + username, e);
                // Continue filter chain - Spring Security will handle authentication failure
            } catch (Exception e) {
                logger.error("JWT token validation failed for user: " + username, e);
                // Continue filter chain - Spring Security will handle authentication failure
            }
        } else if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            // Token was provided but username extraction failed
            logger.warn("JWT token provided but username extraction failed for path: " + requestPath);
        }
        chain.doFilter(request, response);
    }
}
