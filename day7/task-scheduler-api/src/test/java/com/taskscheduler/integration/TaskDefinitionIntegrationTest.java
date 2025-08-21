package com.taskscheduler.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taskscheduler.dto.TaskDefinitionCreateRequest;
import com.taskscheduler.entity.TaskDefinition;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.context.WebApplicationContext;
import org.junit.jupiter.api.BeforeEach;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureWebMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class TaskDefinitionIntegrationTest {
    
    @Autowired
    private WebApplicationContext webApplicationContext;
    
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @BeforeEach
    void setUp() {
        mockMvc = webAppContextSetup(webApplicationContext).build();
    }
    
    @Test
    @Transactional
    void fullWorkflow_CreateAndRetrieveTask() throws Exception {
        TaskDefinitionCreateRequest request = new TaskDefinitionCreateRequest(
            "Integration Test Task",
            "Integration test description",
            "0 0 12 * * ?",
            TaskDefinition.TaskStatus.ACTIVE,
            "com.example.IntegrationTestTask",
            "{\"env\":\"test\"}"
        );
        
        // Create task
        String response = mockMvc.perform(post("/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Integration Test Task"))
                .andReturn().getResponse().getContentAsString();
        
        // Extract ID from response
        var responseObj = objectMapper.readTree(response);
        Long taskId = responseObj.get("id").asLong();
        
        // Retrieve created task
        mockMvc.perform(get("/tasks/" + taskId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(taskId))
                .andExpect(jsonPath("$.name").value("Integration Test Task"));
        
        // List all tasks
        mockMvc.perform(get("/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }
}
