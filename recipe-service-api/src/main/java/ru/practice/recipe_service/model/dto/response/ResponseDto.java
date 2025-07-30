package ru.practice.recipe_service.model.dto.response;

import org.springframework.http.HttpStatus;

public record ResponseDto(HttpStatus status, String body) {
}
