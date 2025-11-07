package ru.practice.parser_service.service.parsers;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Disabled
class ActuatorTest {

    @LocalServerPort
    private int port;

    @Test
    @Disabled
    void healthEndpointShouldBeAvailable() {
        var restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.getForEntity(
                "http://localhost:" + port + "/actuator/health",
                String.class
        );
        assertEquals(200, response.getStatusCode().value());
    }
}