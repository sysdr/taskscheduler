package com.taskscheduler.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.integration.jdbc.lock.DefaultLockRepository;
import org.springframework.integration.jdbc.lock.JdbcLockRegistry;
import org.springframework.integration.jdbc.lock.LockRepository;
import org.springframework.integration.leader.DefaultCandidate;
import org.springframework.integration.redis.util.RedisLockRegistry;
import org.springframework.integration.support.leader.LockRegistryLeaderInitiator;
import org.springframework.integration.support.locks.LockRegistry;
import org.springframework.beans.factory.annotation.Value;

import javax.sql.DataSource;

@Configuration
public class CoordinationConfig {

    @Value("${coordination.leader.election.candidate-id}")
    private String candidateId;

    @Value("${coordination.leader.election.role}")
    private String leaderRole;

    @Bean
    public LockRepository lockRepository(DataSource dataSource) {
        return new DefaultLockRepository(dataSource);
    }

    @Bean("jdbcLockRegistry")
    public LockRegistry jdbcLockRegistry(LockRepository lockRepository) {
        return new JdbcLockRegistry(lockRepository);
    }

    @Bean("redisLockRegistry")
    public LockRegistry redisLockRegistry(RedisConnectionFactory redisConnectionFactory) {
        return new RedisLockRegistry(redisConnectionFactory, "coordination:locks");
    }

    @Bean
    @Primary
    public LockRegistry primaryLockRegistry(RedisConnectionFactory redisConnectionFactory) {
        return redisLockRegistry(redisConnectionFactory);
    }

    @Bean
    public LockRegistryLeaderInitiator leaderInitiator(LockRegistry lockRegistry) {
        LockRegistryLeaderInitiator initiator = new LockRegistryLeaderInitiator(
            lockRegistry, 
            new DefaultCandidate(candidateId, leaderRole)
        );
        initiator.setHeartBeatMillis(5000);
        initiator.setBusyWaitMillis(1000);
        return initiator;
    }
}
