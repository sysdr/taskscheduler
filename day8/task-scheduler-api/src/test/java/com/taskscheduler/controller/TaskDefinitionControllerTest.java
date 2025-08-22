package com.taskscheduler.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taskscheduler.dto.TaskDefinitionCreateRequest;
import com.taskscheduler.dto.TaskDefinitionResponse;
import com.taskscheduler.entity.TaskDefinition;
import com.taskscheduler.service.TaskDefinitionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaskDefinitionController.class)
class TaskDefinitionControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private TaskDefinitionService taskService;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Test
    void createTask_ValidRequest_ShouldReturnCreated() throws Exception {
        TaskDefinitionCreateRequest request = new TaskDefinitionCreateRequest(
            "Test Task",
            "Test Description",
            "0 0 12 * * ?",
            TaskDefinition.TaskStatus.ACTIVE,
            "com.example.TestTask",
            "{\"param1\":\"value1\"}"
        );
        
        // Mock the service response
        TaskDefinitionResponse mockResponse = new TaskDefinitionResponse(
            1L,
            "Test Task",
            "Test Description",
            "0 0 12 * * ?",
            TaskDefinition.TaskStatus.ACTIVE,
            "com.example.TestTask",
            "{\"param1\":\"value1\"}",
            LocalDateTime.now(),
            LocalDateTime.now()
        );
        
        when(taskService.createTask(any(TaskDefinitionCreateRequest.class))).thenReturn(mockResponse);
        
        mockMvc.perform(post("/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }
    
    @Test
    void createTask_InvalidRequest_ShouldReturnBadRequest() throws Exception {
        TaskDefinitionCreateRequest request = new TaskDefinitionCreateRequest(
            "", // Invalid: blank name
            "Test Description",
            "0 0 12 * * ?",
            TaskDefinition.TaskStatus.ACTIVE,
            "com.example.TestTask",
            "{\"param1\":\"value1\"}"
        );
        
        mockMvc.perform(post("/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    void getAllTasks_ShouldReturnOk() throws Exception {
        when(taskService.getAllTasks()).thenReturn(Collections.emptyList());
        
        mockMvc.perform(get("/tasks"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }
    
    @Test
    void healthCheck_ShouldReturnOk() throws Exception {
        mockMvc.perform(get("/tasks/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"));
    }
}
