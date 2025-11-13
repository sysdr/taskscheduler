package com.taskscheduler.batch.service;

import com.taskscheduler.batch.model.Task;
import com.taskscheduler.batch.processor.BatchProcessor;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class BatchAccumulator {
    
    private final BatchProcessor batchProcessor;
    private final BlockingQueue<Task> taskQueue;
    
    @Value("${batch.processor.size:100}")
    private int batchSize;
    
    @Value("${batch.processor.timeout-ms:3000}")
    private long batchTimeoutMs;
    
    @Value("${batch.processor.thread-pool-size:4}")
    private int threadPoolSize;
    
    private volatile boolean running = true;
    
    public BatchAccumulator(BatchProcessor batchProcessor) {
        this.batchProcessor = batchProcessor;
        this.taskQueue = new LinkedBlockingQueue<>(10000);
    }
    
    @PostConstruct
    public void startProcessing() {
        log.info("Starting batch accumulator with batch size: {}, timeout: {}ms, threads: {}",
                batchSize, batchTimeoutMs, threadPoolSize);
        
        for (int i = 0; i < threadPoolSize; i++) {
            Thread.ofVirtual().name("batch-processor-" + i).start(this::processBatches);
        }
    }
    
    public boolean addTask(Task task) {
        try {
            return taskQueue.offer(task, 1, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Interrupted while adding task to queue", e);
            return false;
        }
    }
    
    private void processBatches() {
        List<Task> batch = new ArrayList<>(batchSize);
        long lastBatchTime = System.currentTimeMillis();
        
        while (running) {
            try {
                Task task = taskQueue.poll(100, TimeUnit.MILLISECONDS);
                
                if (task != null) {
                    batch.add(task);
                }
                
                long timeSinceLastBatch = System.currentTimeMillis() - lastBatchTime;
                boolean sizeThresholdReached = batch.size() >= batchSize;
                boolean timeoutReached = timeSinceLastBatch >= batchTimeoutMs && !batch.isEmpty();
                
                if (sizeThresholdReached || timeoutReached) {
                    if (!batch.isEmpty()) {
                        log.debug("Processing batch: size={}, timeout={}", 
                                batch.size(), timeoutReached);
                        batchProcessor.processBatch(new ArrayList<>(batch));
                        batch.clear();
                        lastBatchTime = System.currentTimeMillis();
                    }
                }
                
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.error("Batch processor interrupted", e);
                break;
            } catch (Exception e) {
                log.error("Error in batch processing loop", e);
            }
        }
    }
    
    public void shutdown() {
        running = false;
        log.info("Batch accumulator shutting down");
    }
    
    public int getQueueSize() {
        return taskQueue.size();
    }
}
