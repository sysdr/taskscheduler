package com.scheduler;

import com.scheduler.leader.service.LeaderElectionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class LeaderElectionSchedulerApplication {
    
    private static final Logger logger = LoggerFactory.getLogger(LeaderElectionSchedulerApplication.class);
    
    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(LeaderElectionSchedulerApplication.class, args);
        
        // Add shutdown hook for graceful leadership release
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Shutting down gracefully...");
            LeaderElectionService leaderService = context.getBean(LeaderElectionService.class);
            leaderService.releaseLeadership();
        }));
        
        logger.info("🚀 Leader Election Scheduler started successfully!");
        logger.info("📊 Dashboard available at: http://localhost:8080/dashboard");
        logger.info("🏥 Health check at: http://localhost:8080/actuator/health");
        logger.info("🗃️ H2 Console at: http://localhost:8080/h2-console");
    }
}
