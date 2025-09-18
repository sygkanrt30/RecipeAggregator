package ru.practice.recipe_aggregator.model.dto.factory;

import lombok.experimental.UtilityClass;
import org.springframework.http.HttpStatus;
import ru.practice.recipe_aggregator.model.dto.response.ResponseDto;

@UtilityClass
public class ResponseDtoFactory {
    public ResponseDto getResponseError(HttpStatus status, String reason) {
        return new ResponseDto(status, reason);
    }
}
