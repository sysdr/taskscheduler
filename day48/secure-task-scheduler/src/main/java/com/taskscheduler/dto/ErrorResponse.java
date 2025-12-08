package com.taskscheduler.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
    private String message;
    
    public static ErrorResponse of(String message) {
        return new ErrorResponse(message);
    }
}



