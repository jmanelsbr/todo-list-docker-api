package com.example.demo.dto;

import java.time.LocalDateTime;

public record ApiErrorResponse(
        LocalDateTime now,
        int statusHTTP,
        String error,
        String message,
        String path)
    {



}
