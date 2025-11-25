package com.scheduler.controller;

import com.scheduler.model.LogEntry;
import com.scheduler.service.ElasticsearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/logs")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class LogController {

    private final ElasticsearchService elasticsearchService;

    @GetMapping("/search")
    public List<LogEntry> searchLogs(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String level,
            @RequestParam(required = false) String instance,
            @RequestParam(required = false, defaultValue = "100") Integer size) {
        
        return elasticsearchService.searchLogs(query, level, instance, size);
    }

    @GetMapping("/stats")
    public Map<String, Object> getStats() {
        return elasticsearchService.getLogStats();
    }
}
