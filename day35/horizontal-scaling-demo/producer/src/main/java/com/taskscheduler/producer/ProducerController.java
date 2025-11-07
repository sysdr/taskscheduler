package com.taskscheduler.producer;

import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/producer")
@CrossOrigin(origins = "http://localhost:8082")
public class ProducerController {
    
    private final TaskProducerService producerService;
    
    public ProducerController(TaskProducerService producerService) {
        this.producerService = producerService;
    }
    
    @PostMapping("/task")
    public Map<String, String> produceTask() {
        producerService.produceTask();
        Map<String, String> response = new HashMap<>();
        response.put("status", "Task produced successfully");
        return response;
    }
    
    @PostMapping("/batch")
    public Map<String, String> produceBatch(@RequestParam(defaultValue = "100") int count) {
        producerService.produceBatch(count);
        Map<String, String> response = new HashMap<>();
        response.put("status", "Batch of " + count + " tasks produced");
        return response;
    }
}
