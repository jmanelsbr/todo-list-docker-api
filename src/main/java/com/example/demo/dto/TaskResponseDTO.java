package com.example.demo.dto;

public record TaskResponseDTO(
        Long id,
        String title,
        String description,
        boolean completed
) {
}
