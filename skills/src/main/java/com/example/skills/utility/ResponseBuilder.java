package com.example.skills.utility;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResponseBuilder {

    public static ResponseEntity<Object> buildResponse(int statusCode, String statusMessage, String errorMessage, List<?> responseData) {
        // Create a map to represent the response body
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("statusCode", statusCode);
        responseBody.put("statusMessage", statusMessage);
        responseBody.put("errorMessage", errorMessage);
        responseBody.put("responseData", responseData);

        return ResponseEntity.status(HttpStatus.valueOf(statusCode)).body(responseBody);
    }
}