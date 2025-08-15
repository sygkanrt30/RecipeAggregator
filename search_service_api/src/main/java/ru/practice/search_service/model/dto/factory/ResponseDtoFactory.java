package ru.practice.search_service.model.dto.factory;

import lombok.experimental.UtilityClass;
import org.springframework.http.HttpStatus;
import ru.practice.search_service.model.dto.response.ResponseDto;

@UtilityClass
public class ResponseDtoFactory {
    public ResponseDto getResponseOK() {
        var status = HttpStatus.OK;
        return new ResponseDto(status, status.getReasonPhrase());
    }

    public ResponseDto getResponseError(HttpStatus status, String reason) {
        return new ResponseDto(status, reason);
    }
}
