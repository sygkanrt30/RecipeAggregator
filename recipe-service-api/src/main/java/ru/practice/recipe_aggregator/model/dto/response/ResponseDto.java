package ru.practice.recipe_aggregator.model.dto.response;

import org.springframework.http.HttpStatus;

public record ResponseDto(HttpStatus status, String body) {
}
