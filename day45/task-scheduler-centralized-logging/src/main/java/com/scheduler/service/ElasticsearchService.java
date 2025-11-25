package com.scheduler.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.scheduler.model.LogEntry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class ElasticsearchService {

    private final ElasticsearchClient elasticsearchClient;

    @Value("${elasticsearch.index-prefix}")
    private String indexPrefix;

    private String getCurrentIndex() {
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy.MM.dd"));
        return indexPrefix + "-" + date;
    }

    public List<LogEntry> searchLogs(String searchTerm, String level, String instance, 
                                      Integer size) {
        try {
            List<Query> queries = new ArrayList<>();
            
            if (searchTerm != null && !searchTerm.isEmpty()) {
                queries.add(Query.of(q -> q
                    .multiMatch(m -> m
                        .query(searchTerm)
                        .fields("message", "task_id", "user_id", "correlation_id")
                    )
                ));
            }
            
            if (level != null && !level.isEmpty()) {
                queries.add(Query.of(q -> q
                    .term(t -> t
                        .field("level")
                        .value(level)
                    )
                ));
            }
            
            if (instance != null && !instance.isEmpty()) {
                queries.add(Query.of(q -> q
                    .term(t -> t
                        .field("instance.keyword")
                        .value(instance)
                    )
                ));
            }
            
            Query finalQuery;
            if (queries.isEmpty()) {
                finalQuery = Query.of(q -> q.matchAll(m -> m));
            } else {
                finalQuery = Query.of(q -> q.bool(b -> b.must(queries)));
            }
            
            SearchResponse<Map> response = elasticsearchClient.search(s -> s
                .index(indexPrefix + "-*")
                .query(finalQuery)
                .size(size != null ? size : 100)
                .sort(sort -> sort
                    .field(f -> f
                        .field("@timestamp")
                        .order(co.elastic.clients.elasticsearch._types.SortOrder.Desc)
                    )
                ),
                Map.class
            );

            List<LogEntry> logs = new ArrayList<>();
            for (Hit<Map> hit : response.hits().hits()) {
                Map<String, Object> source = hit.source();
                if (source != null) {
                    logs.add(mapToLogEntry(hit.id(), source));
                }
            }
            
            return logs;
            
        } catch (Exception e) {
            log.error("Error searching logs", e);
            return new ArrayList<>();
        }
    }

    private LogEntry mapToLogEntry(String id, Map<String, Object> source) {
        return LogEntry.builder()
            .id(id)
            .timestamp(source.get("@timestamp") != null ? 
                java.time.Instant.parse(source.get("@timestamp").toString()) : null)
            .level(source.get("level") != null ? source.get("level").toString() : null)
            .service(source.get("service") != null ? source.get("service").toString() : null)
            .instance(source.get("instance") != null ? source.get("instance").toString() : null)
            .correlationId(source.get("correlation_id") != null ? 
                source.get("correlation_id").toString() : null)
            .taskId(source.get("task_id") != null ? source.get("task_id").toString() : null)
            .userId(source.get("user_id") != null ? source.get("user_id").toString() : null)
            .message(source.get("message") != null ? source.get("message").toString() : null)
            .logger(source.get("logger_name") != null ? source.get("logger_name").toString() : null)
            .metadata(new HashMap<>(source))
            .build();
    }

    public Map<String, Object> getLogStats() {
        try {
            Map<String, Object> stats = new HashMap<>();
            
            // Get document count
            var countResponse = elasticsearchClient.count(c -> c
                .index(indexPrefix + "-*")
            );
            stats.put("totalLogs", countResponse.count());
            
            // Get error count
            var errorResponse = elasticsearchClient.count(c -> c
                .index(indexPrefix + "-*")
                .query(q -> q.term(t -> t.field("level").value("ERROR")))
            );
            stats.put("errorCount", errorResponse.count());
            
            return stats;
            
        } catch (Exception e) {
            log.error("Error getting log stats", e);
            return new HashMap<>();
        }
    }
}
