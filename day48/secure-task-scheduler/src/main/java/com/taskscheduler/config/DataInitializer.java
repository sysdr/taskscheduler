package com.taskscheduler.config;

import com.taskscheduler.model.User;
import com.taskscheduler.repository.UserRepository;
import com.taskscheduler.service.DemoDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private DemoDataService demoDataService;
    
    @Override
    @Transactional
    public void run(String... args) {
        // Create admin user
        User admin = null;
        if (!userRepository.existsByUsername("admin")) {
            admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setEmail("admin@taskscheduler.com");
            
            Set<String> adminRoles = new HashSet<>();
            adminRoles.add("USER");
            adminRoles.add("ADMIN");
            admin.setRoles(adminRoles);
            
            admin = userRepository.save(admin);
            System.out.println("✅ Admin user created: admin/admin123");
        } else {
            admin = userRepository.findByUsername("admin").orElse(null);
        }
        
        // Create test user
        User user = null;
        if (!userRepository.existsByUsername("user")) {
            user = new User();
            user.setUsername("user");
            user.setPassword(passwordEncoder.encode("user123"));
            user.setEmail("user@taskscheduler.com");
            
            Set<String> userRoles = new HashSet<>();
            userRoles.add("USER");
            user.setRoles(userRoles);
            
            user = userRepository.save(user);
            System.out.println("✅ Test user created: user/user123");
        } else {
            user = userRepository.findByUsername("user").orElse(null);
        }
        
        // Create demo tasks for admin
        if (admin != null) {
            demoDataService.createDemoTasksForUser(admin);
            System.out.println("✅ Demo tasks created for admin user");
        }
        
        // Create demo tasks for user
        if (user != null) {
            demoDataService.createDemoTasksForUser(user);
            System.out.println("✅ Demo tasks created for test user");
        }
    }
}
