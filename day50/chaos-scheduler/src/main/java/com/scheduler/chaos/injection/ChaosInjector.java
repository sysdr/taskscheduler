package com.scheduler.chaos.injection;

import com.scheduler.chaos.model.ChaosExperiment;
import org.springframework.stereotype.Component;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
public class ChaosInjector {
    private final Random random = new Random();
    private final AtomicBoolean leaderKilled = new AtomicBoolean(false);
    private final AtomicBoolean networkPartitioned = new AtomicBoolean(false);
    private volatile int injectedLatencyMs = 0;
    
    public void injectChaos(ChaosExperiment.ChaosType type, int intensity) {
        switch (type) {
            case LEADER_KILL -> killLeader();
            case NETWORK_PARTITION -> partitionNetwork();
            case LATENCY_INJECTION -> injectLatency(intensity);
            case DATABASE_SLOWDOWN -> slowDatabase(intensity);
            default -> throw new IllegalArgumentException("Unknown chaos type: " + type);
        }
    }
    
    public void stopChaos() {
        leaderKilled.set(false);
        networkPartitioned.set(false);
        injectedLatencyMs = 0;
    }
    
    private void killLeader() {
        leaderKilled.set(true);
        System.out.println("🔥 CHAOS: Leader node killed!");
    }
    
    private void partitionNetwork() {
        networkPartitioned.set(true);
        System.out.println("🔥 CHAOS: Network partitioned!");
    }
    
    private void injectLatency(int latencyMs) {
        injectedLatencyMs = latencyMs;
        System.out.println("🔥 CHAOS: Injecting " + latencyMs + "ms latency!");
    }
    
    private void slowDatabase(int latencyMs) {
        injectedLatencyMs = latencyMs;
        System.out.println("🔥 CHAOS: Database slowdown - " + latencyMs + "ms!");
    }
    
    public boolean isLeaderKilled() {
        return leaderKilled.get();
    }
    
    public boolean isNetworkPartitioned() {
        return networkPartitioned.get();
    }
    
    public int getInjectedLatency() {
        return injectedLatencyMs;
    }
    
    public void maybeInjectLatency() {
        if (injectedLatencyMs > 0) {
            try {
                Thread.sleep(injectedLatencyMs);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
    
    public boolean shouldDropPacket(int dropRate) {
        return random.nextInt(100) < dropRate;
    }
}
