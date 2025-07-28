package ru.practice.parser_service.service.exception;

public class ParserException extends RuntimeException {
    public ParserException(String message, Throwable cause) {
        super(message, cause);
    }
}
