package ru.practice.recipe_aggregator.translator;

import org.springframework.http.HttpStatus;
import ru.practice.recipe_aggregator.exception.ParentException;

public class TranslateException extends ParentException {

    public TranslateException(String message, Throwable cause) {
        super(message, cause, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
