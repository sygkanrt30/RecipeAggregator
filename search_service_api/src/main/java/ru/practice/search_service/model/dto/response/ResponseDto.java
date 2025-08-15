package ru.practice.search_service.model.dto.response;

import org.springframework.http.HttpStatus;

public record ResponseDto(HttpStatus status, String body) {
}
