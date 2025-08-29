package com.example.distributedlock.demo;

import org.springframework.stereotype.Component;

@Component
public class LockingStrategiesDemo {
    
    public void demonstrateDatabaseLocking() {
        System.out.println("📊 Database Locking Strategy:");
        System.out.println("   - Uses SELECT FOR UPDATE or unique constraints");
        System.out.println("   - Pros: Uses existing infrastructure, ACID compliant");
        System.out.println("   - Cons: Database becomes bottleneck, single point of failure");
        System.out.println("   - Best for: Small to medium scale applications");
    }
    
    public void demonstrateZooKeeperLocking() {
        System.out.println("🐘 ZooKeeper Locking Strategy:");
        System.out.println("   - Uses ephemeral sequential nodes for coordination");
        System.out.println("   - Pros: Battle-tested, handles network partitions well");
        System.out.println("   - Cons: Additional infrastructure, complexity");
        System.out.println("   - Best for: Large scale distributed systems");
    }
    
    public void demonstrateRedisLocking() {
        System.out.println("🔴 Redis Locking Strategy:");
        System.out.println("   - Uses SET command with NX (not exists) and EX (expiration)");
        System.out.println("   - Pros: Fast, lightweight, built-in expiration");
        System.out.println("   - Cons: Single point of failure, data loss risk");
        System.out.println("   - Best for: High performance, short-lived locks");
    }
    
    public void showLockingComparison() {
        System.out.println("\n🔍 Locking Strategy Comparison:");
        System.out.println("┌─────────────┬─────────────┬─────────────┬─────────────┐");
        System.out.println("│   Aspect    │  Database   │  ZooKeeper  │    Redis    │");
        System.out.println("├─────────────┼─────────────┼─────────────┼─────────────┤");
        System.out.println("│ Performance │    Medium   │    Medium   │     High    │");
        System.out.println("│ Complexity  │     Low     │     High    │    Medium   │");
        System.out.println("│ Reliability │     High    │   Very High │    Medium   │");
        System.out.println("│ Scalability │     Low     │     High    │     High    │");
        System.out.println("└─────────────┴─────────────┴─────────────┴─────────────┘");
    }
}
