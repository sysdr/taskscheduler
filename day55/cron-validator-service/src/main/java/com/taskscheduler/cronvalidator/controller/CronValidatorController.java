package com.taskscheduler.cronvalidator.controller;

import com.taskscheduler.cronvalidator.dto.*;
import com.taskscheduler.cronvalidator.service.CronValidatorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/cron")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CronValidatorController {
    
    private final CronValidatorService validatorService;
    
    @PostMapping("/validate")
    public ResponseEntity<CronValidationResponse> validate(
            @Valid @RequestBody CronValidationRequest request) {
        log.info("Received validation request for expression: {}", request.getExpression());
        CronValidationResponse response = validatorService.validate(request);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/generate")
    public ResponseEntity<CronGeneratorResponse> generate(
            @Valid @RequestBody CronGeneratorRequest request) {
        log.info("Received generation request for type: {}", request.getType());
        CronGeneratorResponse response = validatorService.generate(request);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/check/{expression}")
    public ResponseEntity<Boolean> quickCheck(@PathVariable String expression) {
        boolean isValid = validatorService.isValidCronExpression(expression);
        return ResponseEntity.ok(isValid);
    }
}
