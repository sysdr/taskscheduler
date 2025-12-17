package com.taskscheduler.cronvalidator;

import com.taskscheduler.cronvalidator.dto.CronGeneratorRequest;
import com.taskscheduler.cronvalidator.dto.CronGeneratorResponse;
import com.taskscheduler.cronvalidator.dto.CronValidationRequest;
import com.taskscheduler.cronvalidator.dto.CronValidationResponse;
import com.taskscheduler.cronvalidator.service.CronValidatorService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CronValidatorServiceTest {

    @Autowired
    private CronValidatorService validatorService;

    @Test
    void testValidExpression() {
        CronValidationRequest request = new CronValidationRequest("0 9 * * MON", "UTC", 5);
        CronValidationResponse response = validatorService.validate(request);
        
        assertTrue(response.isValid());
        assertNotNull(response.getHumanReadable());
        assertEquals(5, response.getNextExecutions().size());
    }

    @Test
    void testInvalidExpression() {
        CronValidationRequest request = new CronValidationRequest("0 25 * * *", "UTC", 5);
        CronValidationResponse response = validatorService.validate(request);
        
        assertFalse(response.isValid());
        assertNotNull(response.getErrorMessage());
    }

    @Test
    void testGenerateDaily() {
        CronGeneratorRequest request = new CronGeneratorRequest();
        request.setType("DAILY");
        request.setMinute("0");
        request.setHour("9");
        
        CronGeneratorResponse response = validatorService.generate(request);
        
        assertEquals("0 9 * * *", response.getExpression());
        assertNotNull(response.getHumanReadable());
    }

    @Test
    void testGenerateWeekly() {
        CronGeneratorRequest request = new CronGeneratorRequest();
        request.setType("WEEKLY");
        request.setMinute("30");
        request.setHour("14");
        request.setDayOfWeek("1");
        
        CronGeneratorResponse response = validatorService.generate(request);
        
        assertEquals("30 14 * * 1", response.getExpression());
    }
}
