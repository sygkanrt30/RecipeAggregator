package ru.practice.recipe_aggregator.recipe_management.model.dto.response;

import org.springframework.http.HttpStatus;

public record ResponseDto(HttpStatus status, String body) {

    public static ResponseDto getResponseError(HttpStatus status, String reason) {
        return new ResponseDto(status, reason);
    }
}
